package com.csdy.item.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomTeleporter {
    // 可排除的危险/特殊维度列表
    private static final List<ResourceLocation> BLACKLIST_DIMENSIONS = List.of(
//            new ResourceLocation("the_nether"), // 下界（可选保留）
//            new ResourceLocation("the_end")      // 末地（可选保留）
    );

    public static void teleportPlayerToRandomDimension(ServerPlayer player) {
        ServerLevel originWorld = player.serverLevel();
        Random random = (Random) player.getRandom();

        // 获取所有已加载且允许传送的维度
        List<ResourceKey<Level>> validDimensions = player.server.levelKeys().stream()
                .filter(dim -> !BLACKLIST_DIMENSIONS.contains(dim.location()))
                .collect(Collectors.toList());

        if (validDimensions.isEmpty()) {
            player.sendSystemMessage(Component.literal("没有可用维度！"));
            return;
        }

        // 随机选择目标维度
        ResourceKey<Level> targetDimension = validDimensions.get(random.nextInt(validDimensions.size()));
        ServerLevel targetWorld = player.server.getLevel(targetDimension);

        // 生成随机坐标（范围可调）
        int x = random.nextInt(60000) - 30000; // ±30,000 区块范围
        int z = random.nextInt(60000) - 30000;

        // 寻找安全着陆点
        BlockPos safePos = findSafePosition(targetWorld, new BlockPos(x, targetWorld.getSeaLevel(), z));

        // 执行传送
        player.teleportTo(
                targetWorld,
                safePos.getX() + 0.5,
                safePos.getY(),
                safePos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );

        // 添加传送特效
        targetWorld.sendParticles(ParticleTypes.PORTAL,
                safePos.getX(), safePos.getY() + 1, safePos.getZ(),
                30, 0.5, 1, 0.5, 0.1);
    }

    private static BlockPos findSafePosition(ServerLevel world, BlockPos startPos) {
//        WorldGenerationContext context = new WorldGenerationContext(world);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(
                startPos.getX(),
                Mth.clamp(startPos.getY(), world.getMinBuildHeight(), world.getMaxBuildHeight()),
                startPos.getZ()
        );

        // 垂直搜索安全位置
        if (world.getBlockState(pos).isAir()) {
            // 向下寻找地面
            while (pos.getY() > world.getMinBuildHeight() + 1 &&
                    world.getBlockState(pos).isAir()) {
                pos.move(Direction.DOWN);
            }
            // 确保站立点安全
            if (!isSafeStandingSpot(world, pos)) {
                // 向上搜索替代方案
                pos.setY(startPos.getY());
                while (pos.getY() < world.getMaxBuildHeight() &&
                        !isSafeStandingSpot(world, pos)) {
                    pos.move(Direction.UP);
                }
            }
        }

        // 最终安全校验
        return isSafeStandingSpot(world, pos) ? pos.immutable() : world.getSharedSpawnPos();
    }

    private static boolean isSafeStandingSpot(ServerLevel world, BlockPos pos) {
        BlockState standingBlock = world.getBlockState(pos);
        BlockState headBlock = world.getBlockState(pos.above());
        BlockState feetBlock = world.getBlockState(pos.below());

        return standingBlock.isAir() &&
                headBlock.isAir() &&
                !feetBlock.isAir() &&
                !feetBlock.getFluidState().is(FluidTags.LAVA);
    }
}

