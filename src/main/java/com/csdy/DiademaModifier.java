package com.csdy;

import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.FollowDiademaMovement;
import mekanism.common.lib.collection.HashList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;

import java.util.ArrayList;
import java.util.List;

import static com.csdy.ModMain.MODID;

///这里不用看,领域强化继承这个类
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class DiademaModifier extends Modifier implements ConditionalStatModifierHook {
    //除了这个可以都不管
    protected abstract DiademaType getDiademaType();

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

    //框架的最后一块拼图
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getLevel() instanceof ServerLevel)) return;
        for (Modifier modifier : getArmorModifiers(player)) {
            if (modifier instanceof DiademaModifier diademaModifier) {
                diademaModifier.getDiademaType().CreateInstance(new FollowDiademaMovement(event.getEntity()));
            }
        }

    }

    private static List<Modifier> getArmorModifiers(Player player) {
        List<Modifier> list = new ArrayList<>();
        // 获取玩家护甲槽
        for (int i = 1; i < 4; i++) {
            ItemStack chestplate = player.getInventory().getArmor(i);
            // 检查是否为匠魂的可修改装备
            if (chestplate.getItem() instanceof ModifiableArmorItem) {
                // 将 ItemStack 转换为 ToolStack
                IToolStackView toolStack = ToolStack.from(chestplate);
                // 获取所有Modifier
                for (ModifierEntry entry : toolStack.getModifierList()) {
                    // 添加至List以比对
                    list.add(entry.getModifier());
                }

            }
        }
        return list;
    }
}
