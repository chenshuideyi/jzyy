package com.csdy.jzyy.modifier.modifier.redstone_component.real;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Disruptor extends NoLevelsModifier implements InventoryTickModifierHook {

    private static final double DISRUPT_RADIUS = 4.0; // 干扰半径

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot || world.isClientSide()) return;
        AABB area = new AABB(holder.getX() - DISRUPT_RADIUS,
                holder.getY() - DISRUPT_RADIUS / 2,
                holder.getZ() - DISRUPT_RADIUS,
                holder.getX() + DISRUPT_RADIUS,
                holder.getY() + DISRUPT_RADIUS / 2,
                holder.getZ() + DISRUPT_RADIUS);

        for (BlockPos pos : BlockPos.betweenClosed(
                BlockPos.containing(area.minX, area.minY, area.minZ),
                BlockPos.containing(area.maxX, area.maxY, area.maxZ))) {
            disruptRedstoneAt(world, pos);
        }
    }

    private void disruptRedstoneAt(Level world, BlockPos pos) {
        if (world.isClientSide()) return;
        BlockState currentState = world.getBlockState(pos); // 使用一个更明确的变量名
        Block block = currentState.getBlock();
        BlockState newState = null; // 用于存储将要设置的新状态，默认为null

        if (block == Blocks.REDSTONE_TORCH) {
            if (currentState.hasProperty(BlockStateProperties.LIT) && currentState.getValue(BlockStateProperties.LIT)) {
                newState = currentState.setValue(BlockStateProperties.LIT, false);
            }
        } else if (block == Blocks.REDSTONE_WIRE) {
            if (currentState.hasProperty(BlockStateProperties.POWER) && currentState.getValue(BlockStateProperties.POWER) > 0) {
                newState = currentState.setValue(BlockStateProperties.POWER, 0);
            }
        }
        if (newState != null) world.setBlock(pos, newState, 3);

    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
