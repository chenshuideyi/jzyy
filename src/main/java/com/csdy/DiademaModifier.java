package com.csdy;

import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.csdy.ModMain.MODID;

/// 这里不用看,领域强化继承这个类
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class DiademaModifier extends Modifier implements EquipmentChangeModifierHook, ConditionalStatModifierHook {
    private static final ResourceLocation DIADEMA_UUID = new ResourceLocation(MODID, "diadema_uuid");

    //除了这个可以都不管
    protected abstract DiademaType getDiademaType();

    //与护甲领域的对应字典
    private static final Map<UUID, Diadema> Diademas = new HashMap<>();

    //创建领域 最最后一块拼图
    //解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了!!!
    private static void createDiadema(ModDataNBT nbt, DiademaType diademaType, Entity entity) {
        UUID uuid = getUuid(nbt);
        var current = Diademas.get(uuid);
        if (current != null && current.isAlive()) return;
        var diadema = diademaType.CreateInstance(new FollowDiademaMovement(entity));
        Diademas.put(uuid, diadema);
    }

    private static @NotNull UUID getUuid(ModDataNBT nbt) {
        UUID uuid;
        // 骑士史莱姆和mojang写的抽象api一套组合拳下来让我不得不硬编码一个11来表示IntArray的类型
        // 如果以后要升级版本的话只要mojang的api改一下就会因为这个出现未定义行为，到时候可能得找半天才能找到是这里的问题
        // 虽然只要mojang的开发者脑子正常就不会这样做，但是还是太恶俗了，保险起见留个注释吧
        if (!nbt.contains(DIADEMA_UUID, 11)) {
            uuid = UUID.randomUUID();
            nbt.put(DIADEMA_UUID, NbtUtils.createUUID(uuid));
            // 打印调试信息
            System.out.println("附加随机 UUID: " + uuid);
        } else {
            uuid = NbtUtils.loadUUID(nbt.get(DIADEMA_UUID));
            // 打印调试信息
            System.out.println("已拥有 UUID: " + nbt.get(DIADEMA_UUID));
        }
        return uuid;
    }

    public boolean isNoLevels() {
        return true;
    }

    @Override
    public float modifyStat(@NotNull IToolStackView iToolStackView, @NotNull ModifierEntry modifierEntry, @NotNull LivingEntity livingEntity,
                            @NotNull FloatToolStat floatToolStat, float v, float v1) {
        return 0;
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        if (isNoLevels()) {
            return super.getDisplayName();
        } else {
            return super.getDisplayName(level);
        }
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        super.registerHooks(hookBuilder);
    }

    //框架的最后一块拼图
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getLevel() instanceof ServerLevel)) return;
        getArmors(player).forEach(armor -> {
            var nbt = armor.getPersistentData();
            getDiademaModifiers(armor).forEach(diademaModifier -> createDiadema(nbt, diademaModifier.getDiademaType(), player));
        });
    }

    private static Stream<ToolStack> getArmors(Player player) {
        return IntStream.of(0, 1, 2, 3).mapToObj(i -> { // 从四个装备槽获取装备
            ItemStack armorplate = player.getInventory().getArmor(i);
            // 检查是否为匠魂的可修改装备，不是则返回null
            if (armorplate.getItem() instanceof ModifiableArmorItem armor) {
                return ToolStack.from(armorplate);
            }
            return null;
        }).filter(Objects::nonNull); // 过滤掉null，返回
    }

    private static Stream<DiademaModifier> getDiademaModifiers(IToolStackView toolStack) {
        return toolStack.getModifierList().stream().map(entry -> {
            if (entry.getModifier() instanceof DiademaModifier diademaModifier) return diademaModifier;
            return null;
        }).filter(Objects::nonNull); // 故技重施
    }

    @Override
    public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!(context.getLevel() instanceof ServerLevel)) return;
        LivingEntity entity = context.getEntity();
        if (!(entity instanceof Player)) return;
        if (!(entry.getModifier() instanceof DiademaModifier diademaModifier)) return;

        if (context.getChangedSlot().isArmor()) {
            ModDataNBT nbt = tool.getPersistentData();
            createDiadema(nbt, diademaModifier.getDiademaType(), entity);
        }
    }

    @Override
    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!(context.getLevel() instanceof ServerLevel)) return;
        if (!(context.getChangedSlot().isArmor())) return;
        if (!(entry.getModifier() instanceof DiademaModifier)) return;

        var uuid = getUuid(tool.getPersistentData());
        if (!Diademas.containsKey(uuid)) return;
        Diademas.get(uuid).kill();
        Diademas.remove(uuid);
    }
}
