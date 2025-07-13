package com.csdy.jzyy.modifier.modifier.living_wood.real;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.*;

public class ForestArmor extends NoLevelsModifier implements InventoryTickModifierHook {

    // 在mod主类或全局常量中定义配置
    public static final int TREE_CHECK_INTERVAL = 100; // 每5秒检测一次(100 ticks)
    public static final int MAX_HEAL_TREES = 20;      // 最大计算树木数量
    public static final float HEAL_PER_TREE = 0.005f; // 每棵树每tick恢复量
    public static final int RADIUS = 10;              // 检测半径

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot || world.isClientSide()) return;

        // 使用WeakHashMap缓存减少内存占用
        Map<UUID, TreeHealCache> cache = ModCache.getTreeHealCache();
        long gameTime = world.getGameTime();

        // 获取或创建缓存
        TreeHealCache holderCache = cache.computeIfAbsent(holder.getUUID(),
                uuid -> new TreeHealCache(0, gameTime));

        // 定期更新树木计数(降低性能消耗)
        if (gameTime - holderCache.lastCheckTime >= TREE_CHECK_INTERVAL) {
            holderCache.treeCount = countValidTrees(world, holder.blockPosition());
            holderCache.lastCheckTime = gameTime;
        }

        // 计算恢复量(树木越多恢复越快)
        if (holderCache.treeCount > 0) {
            float healAmount = Math.min(holderCache.treeCount, MAX_HEAL_TREES) * HEAL_PER_TREE;
            if (holder.getHealth() < holder.getMaxHealth()) {
                holder.heal(healAmount);
            }
        }
    }

    // 缓存数据结构
    public static class TreeHealCache {
        public int treeCount;
        public long lastCheckTime;

        public TreeHealCache(int count, long time) {
            this.treeCount = count;
            this.lastCheckTime = time;
        }
    }

    // 带结构的树木检测(避免把散落的原木算作树)
    private int countValidTrees(Level world, BlockPos center) {
        Set<BlockPos> checkedLogs = new HashSet<>();
        int treeCount = 0;

        // 检测范围内的原木
        for (BlockPos pos : BlockPos.withinManhattan(center, RADIUS, RADIUS/2, RADIUS)) {
            if (world.getBlockState(pos).is(BlockTags.LOGS) && !checkedLogs.contains(pos)) {
                if (isCompleteTree(world, pos, checkedLogs)) {
                    treeCount++;
                    if (treeCount >= MAX_HEAL_TREES) break; // 达到上限停止检测
                }
            }
        }
        return treeCount;
    }

    // 检测完整树木结构(至少包含5个相连原木和2个树叶方块)
    private boolean isCompleteTree(Level world, BlockPos startPos, Set<BlockPos> checkedLogs) {
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);
        int logCount = 0;
        int leafCount = 0;

        while (!queue.isEmpty() && logCount < 20) { // 防止无限循环
            BlockPos pos = queue.poll();

            if (checkedLogs.contains(pos)) continue;
            checkedLogs.add(pos);

            if (world.getBlockState(pos).is(BlockTags.LOGS)) {
                logCount++;
                // 检查相邻方块
                for (Direction dir : Direction.values()) {
                    queue.add(pos.relative(dir));
                }
            }
            else if (world.getBlockState(pos).is(BlockTags.LEAVES)) {
                leafCount++;
            }
        }

        return logCount >= 5 && leafCount >= 2; // 完整树木标准
    }

    // 单例缓存管理
    public static class ModCache {
        private static final WeakHashMap<UUID, TreeHealCache> treeHealCache = new WeakHashMap<>();
        public static Map<UUID, TreeHealCache> getTreeHealCache() {
            return treeHealCache;
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
