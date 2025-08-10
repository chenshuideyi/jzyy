package com.csdy.jzyy.event;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.item.tool.tinker_loli_pickaxe;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningTierToolHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakEventHandler {

    /**
     * 这个方法会在任何方块被破坏时由Forge自动调用。
     * @param event 包含了事件所有信息的对象
     */
    @SubscribeEvent
    public static void onBlockBroken(final BlockEvent.BreakEvent event) {

        // 获取玩家，如果不是玩家破坏的，直接返回
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        // 获取玩家手持的物品
        ItemStack mainHandStack = player.getMainHandItem();

        // ★★★ 核心检查：如果玩家手里拿的不是我们的特殊镐子，就立刻退出 ★★★
        if (!(mainHandStack.getItem() instanceof tinker_loli_pickaxe)) {
            return;
        }

        // --- 2. 如果检查通过，执行我们的范围破坏逻辑 ---

        ToolStack tool = ToolStack.from(mainHandStack);
        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos center = event.getPos();

        // 调用我们已经写好的、简单直接的批量破坏方法
        batchMineInManhattanRadius(tool, level, player, center);
    }

    public static void batchMineInManhattanRadius(ToolStack tool, ServerLevel level, ServerPlayer player, BlockPos center) {
        int expandedLevel = tool.getModifierLevel(TinkerModifiers.expanded.getId());
        if (expandedLevel == 0) {
            return;
        }

        Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
        ItemStack toolStack = player.getMainHandItem(); // 获取实际手持物品

        for (int dx = -expandedLevel; dx <= expandedLevel; dx++) {
            for (int dy = -expandedLevel; dy <= expandedLevel; dy++) {
                for (int dz = -expandedLevel; dz <= expandedLevel; dz++) {
                    int manhattanDistance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (manhattanDistance > expandedLevel || manhattanDistance == 0) {
                        continue;
                    }

                    BlockPos targetPos = center.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(targetPos);

                    // 跳过空气、不可破坏方块和硬度为-1的方块
                    if (targetState.isAir() ||
                            targetState.getDestroySpeed(level, targetPos) < 0) {
                        continue;
                    }

                    // 检查工具等级是否足够且方块可以被正确挖掘
                    if (TierSortingRegistry.isCorrectTierForDrops(tier, targetState) &&
                            toolStack.isCorrectToolForDrops(targetState)) {
                        level.destroyBlock(targetPos, true, player);
                    }
                }
            }
        }
    }
}
