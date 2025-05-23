package com.csdy.jzyy.modifier.modifier.glass;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isEntityInSunlight;

public class Loupe extends NoLevelsModifier implements InventoryTickModifierHook {

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (isCorrectSlot && isEntityInSunlight(holder)) {
            burnEntitiesUnderPlayer(holder);
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

    private static void burnEntitiesUnderPlayer(LivingEntity holder) {

        Level level = holder.level();
        BlockPos playerPos = holder.blockPosition();

        // 检查玩家脚下 3x3 区域（Y-1 层）是否有生物
        AABB checkArea = new AABB(
                playerPos.getX() - 1.5, playerPos.getY() - 1, playerPos.getZ() - 1.5,
                playerPos.getX() + 1.5, playerPos.getY(), playerPos.getZ() + 1.5
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                checkArea,
                entity -> entity != holder // 排除玩家自己
        );

        // 遍历所有符合条件的生物
        for (LivingEntity entity : entities) {
            // 检查生物是否暴露在阳光下（头顶无遮挡）
            BlockPos entityPos = entity.blockPosition();
            if (level.canSeeSky(entityPos)) {
                entity.setSecondsOnFire(10);
            }
        }
    }
}
