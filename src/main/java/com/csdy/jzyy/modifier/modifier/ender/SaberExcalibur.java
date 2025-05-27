package com.csdy.jzyy.modifier.modifier.ender;

import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.ExcaliburPacket;
import com.csdy.tcondiadema.ParticleUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.awt.*;
import java.util.Collections;
import java.util.Set;

public class SaberExcalibur extends NoLevelsModifier implements GeneralInteractionModifierHook, InventoryTickModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level level, LivingEntity livingEntity, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        stack.setHoverName(Component.nullToEmpty("誓约胜利之剑"));
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry entry, Player player, InteractionHand hand, InteractionSource interactionSource) {
        if (!tool.isBroken()) {
            GeneralInteractionModifierHook.startUsing(tool, entry.getId(), player, hand);
            return InteractionResult.CONSUME;
        }else return InteractionResult.PASS;

    }

    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry entry, LivingEntity living, int timeLeft) {
        if (!(living instanceof Player player)) return;

        int usedTicks = getUseDuration(tool, entry) - timeLeft;

        if (!player.level().isClientSide()) {
            JzyySyncing.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new ExcaliburPacket(player.getEyePosition())
            );
        }

        // 客户端消息
        if (player.level.isClientSide) {
            if (usedTicks == 80) { // 精确 5 秒（100 ticks）
                player.displayClientMessage(
                        Component.literal("汇聚的星之吐息").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            else if (usedTicks == 120) { // 精确 7 秒（140 ticks）
                player.displayClientMessage(
                        Component.literal("闪耀的生命奔流").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            else if (usedTicks == 165) { // 精确 7.5 秒（150 ticks）
                player.displayClientMessage(
                        Component.literal("接下吧").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
        }

    }

        @Override
        public UseAnim getUseAction (IToolStackView tool, ModifierEntry modifier){
            return UseAnim.BOW;
        }

        @Override
        public int getUseDuration (IToolStackView tool, ModifierEntry modifier){
            return 180;
        }

        @Override
        public void onFinishUsing (IToolStackView tool, ModifierEntry modifier, LivingEntity entity){
            if (!(entity instanceof Player player)) return;
            tool.setDamage(2147483647);
            int radius = 160; // 破坏半径
            float angle = 50; // 角度
            int height = 60;  // 高度
            if (player.level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Excalibur!!!").withStyle(ChatFormatting.YELLOW),
                        false
                );
                spawnParticles(player, radius, angle, height);
            }
            if (!player.level.isClientSide()) {
                destroyBlocksInSector(player, radius, angle, height);
                damageEntitiesInSector(player, radius, angle, height,50);
            }
        }

    /**
     * 在玩家面前创建一个圆锥形的破坏区域。
     * 锥尖大致在玩家面前，底部向外展开。
     *
     * @param player 触发者
     * @param radius 圆锥的长度（从玩家面前开始计算的深度）
     * @param angleDegrees 圆锥底部的最大宽度 (X方向直径)
     * @param height 圆锥底部的最大高度 (Y方向直径)
     */
    public void destroyBlocksInSector(Player player, int radius, float angleDegrees, int height) {
        Level level = player.level;

        // 获取玩家位置和朝向
        Vec3 playerPos = player.position();
        Vec3 lookAngle = player.getLookAngle().normalize();

        // 计算扇形角度范围
        float halfAngle = angleDegrees / 2;
        double angleRad = Math.toRadians(halfAngle);
        double cosThreshold = Math.cos(angleRad);

        // 获取玩家所在的方块位置
        BlockPos playerBlockPos = player.blockPosition();

        // 遍历高度范围内的每一层
        for (int yOffset = 0; yOffset <= height; yOffset++) {
            // 计算当前层的半径（随着高度增加而减小）
            double currentRadius = radius * (1 - (double)yOffset / height);
            int currentRadiusInt = (int)Math.ceil(currentRadius);

            // 遍历当前层的圆形区域
            for (int xOffset = -currentRadiusInt; xOffset <= currentRadiusInt; xOffset++) {
                for (int zOffset = -currentRadiusInt; zOffset <= currentRadiusInt; zOffset++) {
                    // 跳过超出当前层半径的方块
                    if (xOffset * xOffset + zOffset * zOffset > currentRadius * currentRadius) {
                        continue;
                    }

                    BlockPos targetPos = playerBlockPos.offset(xOffset, yOffset, zOffset);

                    // 计算从玩家到方块的向量
                    Vec3 toBlock = new Vec3(
                            targetPos.getX() + 0.5 - playerPos.x,
                            0,
                            targetPos.getZ() + 0.5 - playerPos.z
                    ).normalize();

                    // 计算点积
                    double dotProduct = lookAngle.x * toBlock.x + lookAngle.z * toBlock.z;

                    if (dotProduct >= cosThreshold) {
                        // 只破坏可破坏的固体方块
                        BlockState state = level.getBlockState(targetPos);
                        if (state.getDestroySpeed(level, targetPos) >= 0 && !state.isAir()) {
                            level.destroyBlock(targetPos, false, player);
                        }
                    }
                }
            }
        }
    }

    public void damageEntitiesInSector(Player player, int radius, float angleDegrees, int height, float damage) {
        Level level = player.level;
        Vec3 playerPos = player.position();
        Vec3 lookAngle = player.getLookAngle().normalize();
        float halfAngle = angleDegrees / 2;
        double angleRad = Math.toRadians(halfAngle);
        double cosThreshold = Math.cos(angleRad);

        AABB area = new AABB(
                playerPos.x - radius, playerPos.y, playerPos.z - radius,
                playerPos.x + radius, playerPos.y + height, playerPos.z + radius
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (target == player) continue; // Skip the player themselves

            Vec3 toEntity = target.position().subtract(playerPos).normalize();

            // Check if entity is within the angle
            double dotProduct = lookAngle.x * toEntity.x + lookAngle.z * toEntity.z;
            if (dotProduct >= cosThreshold) {
                // Check vertical angle
                double verticalRatio = (target.getY() - playerPos.y) / height;
                if (Math.abs(verticalRatio) <= 1.0) {
                    // Check distance
                    double distance = playerPos.distanceTo(target.position());
                    if (distance <= radius) {
                        target.hurt(player.damageSources().playerAttack(player), damage);
                    }
                }
            }
        }
    }

    private void spawnParticles(Player player, int radius, float angleDegrees, int height) {
        // 确保只在客户端执行粒子效果
        if (player.level.isClientSide()) {
            Vec3 playerEyePos = player.position().add(0, player.getEyeHeight() - 0.5, 0); // 玩家眼睛（或手部）的起始位置
            Vec3 lookVec = player.getLookAngle().normalize(); // 玩家的视线向量
            RandomSource random = player.level.random;

            float powerScale = 2.5f;    // 整体效果放大系数

            SimpleParticleType types = ParticleTypes.EXPLOSION_EMITTER;

            // 1. 计算光束的目标水平位置 (X, Z) 和大致的冲击点 Y
            //    光束将从这个点的正上方开始下落
            Vec3 targetImpactPoint = playerEyePos.add(lookVec.scale(radius));

            // 2. 定义光束的起始Y坐标和结束Y坐标
            //    光束从目标点上方 'height' 的位置开始
            double beamStartY = targetImpactPoint.y + height;
            //    光束结束在目标点的Y坐标 (或略高于，避免穿地)
            double beamEndY = targetImpactPoint.y;

            // 如果玩家向上看导致 beamStartY 低于 beamEndY，则修正
            if (beamStartY < beamEndY + 1.0) { // 至少保证1个单位的高度
                beamStartY = beamEndY + height;
            }

            ParticleUtils.Drawline(1,targetImpactPoint.x,targetImpactPoint.y,targetImpactPoint.z,player.getX(),player.getY(),player.getZ(),types,player.level);

            int explosionParticleCount = (int) (30 * powerScale); // 根据 powerScale 增加粒子数量
            for (int i = 0; i < explosionParticleCount; i++) {
                // EXPLOSION_EMITTER 产生的是烟雾和火花，适合冲击效果
                player.level.addParticle(types,
                        targetImpactPoint.x,
                        targetImpactPoint.y + (random.nextDouble() - 0.2) * 0.5 * height * 0.1, // 爆炸略微向上散开
                        targetImpactPoint.z,
                        (random.nextDouble() - 0.5) * 1.5 * powerScale, // 爆炸范围和速度受 powerScale 影响
                        (random.nextDouble() - 0.5) * 1.5 * powerScale,
                        (random.nextDouble() - 0.5) * 1.5 * powerScale);
            }
        }
    }

        @Override
        protected void registerHooks (ModuleHookMap.Builder hookBuilder){
            hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
        }


}
