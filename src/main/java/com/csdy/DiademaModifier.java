package com.csdy;

import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.FollowDiademaMovement;
import com.csdy.item.register.ItemRegister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;

import javax.annotation.Nullable;
import java.util.*;

import static com.csdy.ModMain.MODID;

///这里不用看,领域强化继承这个类
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class DiademaModifier extends Modifier implements EquipmentChangeModifierHook,ConditionalStatModifierHook {
    private final ResourceLocation DIADEMAUUID = new ResourceLocation(MODID, "diadema_uuid");
    //除了这个可以都不管
    protected abstract DiademaType getDiademaType();

    //与护甲领域的对应字典
    private static final Map <ModifierEntry,Diadema> Diademas = new HashMap<>();

    //创建领域 最最后一块拼图
    //解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了解决了!!!
    private static void createDiadema(ModifierEntry entry, Entity entity){
        if(!(entry.getModifier() instanceof DiademaModifier diademaModifier)) return;
        var current = Diademas.get(entry);
        if (current != null && current.isAlive()) return;
        var diadema = diademaModifier.getDiademaType().CreateInstance(new FollowDiademaMovement(entity));
        Diademas.put(entry,diadema);
    }


    //移除领域 最最最后一块拼图 解决多人及同词条冲突问题
    private static void removeDiadema(ModifierEntry entry, Entity entity){
        if(!Diademas.containsKey(entry)) return;

        if(!(entry.getModifier() instanceof DiademaModifier diademaModifier)) return;
        Diademas.get(entry).kill();
        Diademas.remove(entry);
    }

    public boolean isNoLevels() {
        return true;
    }

    @Override
    public float modifyStat(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, FloatToolStat floatToolStat, float v, float v1) {
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
        hookBuilder.addHook(this,ModifierHooks.EQUIPMENT_CHANGE);
        super.registerHooks(hookBuilder);
    }

    //框架的最后一块拼图
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getLevel() instanceof ServerLevel)) return;
        getArmorModifiers(player).forEach(entry -> createDiadema(entry,player));
//        System.out.println(player.getMainHandItem().hashCode());
    }

    private static List<ModifierEntry> getArmorModifiers(Player player) {
        List<ModifierEntry> list = new ArrayList<>();
        // 获取玩家护甲槽
        for (int i = 1; i < 4; i++) {
            ItemStack armorplate = player.getInventory().getArmor(i);
            // 检查是否为匠魂的可修改装备
            if (armorplate.getItem() instanceof ModifiableArmorItem) {
                // 将 ItemStack 转换为 ToolStack
                IToolStackView toolStack = ToolStack.from(armorplate);
                // 获取所有Modifier
                list.addAll(toolStack.getModifierList());

            }
        }
        return list;
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context) {
        String adduuid = "adduuid";
        LivingEntity entity = context.getEntity();
        if (!(entity instanceof Player player)) return;
        if (context.getLevel() instanceof ServerLevel) {
            if (context.getChangedSlot().isArmor()) {
                ModDataNBT nbt = tool.getPersistentData();
                if (!nbt.contains(DIADEMAUUID, CompoundTag.TAG_INT_ARRAY)) {
                    // 生成一个随机的 UUID
                    UUID uuid = UUID.randomUUID();
                    nbt.getCompound(DIADEMAUUID).putUUID(adduuid,uuid);

                    // 打印调试信息
                    System.out.println("附加随机 UUID: " + uuid);
                } else {
                    // 打印调试信息
                    System.out.println("已拥有 UUID: " + nbt.get(DIADEMAUUID));
                }
                createDiadema(entry, entity);
            }

        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context) {
        if(!Diademas.containsKey(entry)) return;
        if (!(context.getChangedSlot().isArmor())) return;
        if(!(entry.getModifier() instanceof DiademaModifier)) return;
        Diademas.get(entry).kill();
        Diademas.remove(entry);
    }


}
