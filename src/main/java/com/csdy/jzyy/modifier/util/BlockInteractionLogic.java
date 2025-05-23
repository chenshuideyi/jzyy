package com.csdy.jzyy.modifier.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockInteractionLogic {


    // 魔法数字常量
    private static final int SINGLE_BLOCK_DROP_MULTIPLIER = 1; // 单个方块掉落倍数
    private static final int ITEM_PICKUP_DELAY = 10;              // 物品拾取延迟
    private static final double RAYCAST_DISTANCE = 64.0;          // 射线投射距离

    /**
     *
     * @param world  世界访问器
     * @param x      初始触发X坐标
     * @param y      初始触发Y坐标
     * @param z      初始触发Z坐标
     * @param entity 触发动作的实体
     */
    public static void forceBreakBlock(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        BlockPos triggerPos = BlockPos.containing(x, y, z);
        ServerLevel serverLevel = (world instanceof ServerLevel) ? (ServerLevel) world : null;
            BlockState targetBlockState = world.getBlockState(triggerPos);
            Block targetBlock = targetBlockState.getBlock();

            // 避免为空气生成物品
            if (!targetBlockState.isAir()) {
                ItemStack itemStackToSpawn = new ItemStack(targetBlock);
                if (serverLevel != null) {
                    for (int i = 0; i < SINGLE_BLOCK_DROP_MULTIPLIER; ++i) {
                        spawnItemEntity(serverLevel, x, y, z, itemStackToSpawn.copy());
                    }
                }
            }

        world.destroyBlock(triggerPos, false);
    }


    /**
     * 辅助方法：从实体视角执行方块射线投射
     *
     * @param world  世界访问器
     * @param entity 执行射线投射的实体
     * @return 方块命中结果
     */
    private static BlockHitResult performRaycast(LevelAccessor world, Entity entity) {
        Vec3 eyePos = entity.getEyePosition(1.0F);
        Vec3 lookVec = entity.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(RAYCAST_DISTANCE));
        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE, // 与原版一致：考虑方块形状
                ClipContext.Fluid.NONE,    // 与原版一致：忽略流体
                entity                     // 实体上下文（用于碰撞检查等）
        );
        // 使用world.clip而非entity.level().clip保持一致性
        return world.clip(context);
    }

    /**
     * 辅助方法：生成物品实体
     *
     * @param level 要生成物品的服务端世界
     * @param x     生成X坐标
     * @param y     生成Y坐标
     * @param z     生成Z坐标
     * @param stack 物品堆栈
     */
    private static void spawnItemEntity(ServerLevel level, double x, double y, double z, ItemStack stack) {
        if (stack.isEmpty()) return; // 不为空堆栈生成实体

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
        itemEntity.setPickUpDelay(ITEM_PICKUP_DELAY);
        level.addFreshEntity(itemEntity);
    }
}
