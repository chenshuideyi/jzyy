package com.csdy.jzyy.modifier.modifier.tian_yi;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.repairItem;

public class Reality extends NoLevelsModifier implements InventoryTickModifierHook {

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        // 只在服务端执行，避免客户端也执行
        if (world.isClientSide) return;

        // 确保持有者是玩家
        if (!(holder instanceof Player player)) return;

        // 检查玩家是否掉进虚空（Y坐标低于世界底部）
        if (player.getY() < world.getMinBuildHeight()) {
            // 获取玩家当前位置
            BlockPos playerPos = player.blockPosition();

            // 寻找安全的传送位置（从世界底部向上搜索）
            BlockPos safePos = findSafePosition(world, playerPos);

            if (safePos != null) {
                // 传送玩家到安全位置
                player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
                // 防止摔落伤害
                player.fallDistance = 0;
            }
        }
    }

    /**
     * 寻找安全的传送位置
     */
    private BlockPos findSafePosition(Level world, BlockPos startPos) {
        // 从世界底部开始向上搜索安全位置
        int searchRadius = 16; // 搜索半径
        int maxSearchHeight = world.getMaxBuildHeight(); // 最大搜索高度

        // 优先在玩家XZ坐标附近搜索
        for (int y = world.getMinBuildHeight(); y < maxSearchHeight; y++) {
            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = new BlockPos(startPos.getX() + x, y, startPos.getZ() + z);

                    // 检查这个位置是否安全（有固体方块且上方有空间）
                    if (isSafePosition(world, checkPos)) {
                        return checkPos.above(); // 返回方块上方的位置
                    }
                }
            }
        }

        // 如果没找到，返回世界出生点
        return world.getSharedSpawnPos();
    }

    /**
     * 检查位置是否安全
     */
    private boolean isSafePosition(Level world, BlockPos pos) {
        // 检查脚下方块是否坚固
        if (!world.getBlockState(pos).isSolid()) {
            return false;
        }

        // 检查站立位置是否有空间（2格高）
        if (!world.getBlockState(pos.above()).isAir() || !world.getBlockState(pos.above(2)).isAir()) {
            return false;
        }

        // 检查这个位置是否安全（不是液体、不是危险方块等）
        return world.getBlockState(pos).isAir();
    }



    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
