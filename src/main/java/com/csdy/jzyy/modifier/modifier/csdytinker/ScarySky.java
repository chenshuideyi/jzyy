package com.csdy.jzyy.modifier.modifier.csdytinker;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.LeftClickModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


public class ScarySky extends NoLevelsModifier implements LeftClickModifierHook {

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        if (!level.isClientSide) {
            ScarySky(player);
        }
    }

    @Override
    public void onLeftClickBlock(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        if (!level.isClientSide) {
            ScarySky(player);
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.LEFT_CLICK);
    }

    public boolean ScarySky(Player player) {

    Level world = player.level; // 直接用 player.getLevel() 更清晰

    Direction facing = player.getDirection(); // 获取玩家的水平朝向 (NORTH, SOUTH, EAST, WEST)
    BlockPos playerPos = player.blockPosition(); // 获取玩家脚下的方块坐标

    int pX = playerPos.getX();
    int pY = playerPos.getY();
    int pZ = playerPos.getZ();

    // 定义清除区域的Y轴范围
    int minY = pY - 256;
    int maxY = pY + 128;

    // 定义清除区域的X和Z轴边界
    // 这些值是基于你原始代码的意图推断和修正的
    int x1, z1, x2, z2;

    switch (facing) {
        case NORTH: // Z 减小方向
            x1 = pX - 64;
            x2 = pX + 64;
            z1 = pZ - 64; // 清除玩家前方64格
            z2 = pZ;      // 清除到玩家所在的Z坐标（不包括玩家脚下，如果想包括，可以是 pZ-1 或 pZ）
            // 原始代码: Z-64 到 Z (player.getZ()+0)
            break;
        case SOUTH: // Z 增大方向
            x1 = pX - 64;
            x2 = pX + 64;
            z1 = pZ;      // 从玩家所在的Z坐标开始清除
            z2 = pZ + 64; // 清除玩家前方64格
            // 原始代码: Z (player.getZ()-0) 到 Z+64
            break;
        case WEST:  // X 减小方向
            x1 = pX - 64; // 清除玩家前方64格
            x2 = pX;      // 清除到玩家所在的X坐标
            z1 = pZ - 64;
            z2 = pZ + 64;
            // 原始代码: X-64 到 X (player.getX()+0)
            break;
        case EAST:  // X 增大方向
            x1 = pX;      // 从玩家所在的X坐标开始清除
            x2 = pX + 64; // 清除玩家前方64格
            z1 = pZ - 64;
            z2 = pZ + 64;
            // 原始代码 (修正后): X (player.getX()+0) 到 X+64
            break;
        default:
            // facing.getDirection() 只会返回 NORTH, SOUTH, EAST, WEST
            // 不会进入 default, 但为了完整性可以保留
            return false;
    }

    // 创建AABB（确保坐标是min到max的顺序，AABB构造函数会自动处理）
    // BlockPos.betweenClosedStream 会处理好顺序问题，但明确一下更好
    BlockPos corner1 = new BlockPos(Math.min(x1, x2), minY, Math.min(z1, z2));
    BlockPos corner2 = new BlockPos(Math.max(x1, x2), maxY, Math.max(z1, z2));
    AABB areaToClear = new AABB(corner1, corner2);

    BlockPos.betweenClosedStream(areaToClear).forEach((blockPos) -> {
        world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3); // 使用 flag 3 (1 | 2) 来通知客户端并更新邻近方块

    });

    return true;
 }
}
