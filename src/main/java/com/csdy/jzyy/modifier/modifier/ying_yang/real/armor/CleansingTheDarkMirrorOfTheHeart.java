package com.csdy.jzyy.modifier.modifier.ying_yang.real.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class CleansingTheDarkMirrorOfTheHeart extends NoLevelsModifier implements InventoryTickModifierHook {
    ///涤除玄览

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (!(player.tickCount % 660 == 0)) return;
        if (!isCorrectSlot) return;

        // 清理所有掉落物（整个世界）
        if (!world.isClientSide) {
            // 获取所有掉落物实体
            List<ItemEntity> allItemEntities = world.getEntitiesOfClass(ItemEntity.class,
                    new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000)); // 整个世界的范围

            for (ItemEntity itemEntity : allItemEntities) {
                itemEntity.discard(); // 移除掉落物实体
            }

            // 可选：发送清理消息
            player.sendSystemMessage(Component.literal("已清理所有掉落物"));
        }
    }



    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
