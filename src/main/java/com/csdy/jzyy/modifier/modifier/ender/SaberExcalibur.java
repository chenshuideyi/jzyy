package com.csdy.jzyy.modifier.modifier.ender;

import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.ExcaliburPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SaberExcalibur extends NoLevelsModifier implements GeneralInteractionModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
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
            else if (usedTicks == 160) { // 精确 7.5 秒（150 ticks）
                player.displayClientMessage(
                        Component.literal("接下吧").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
        }

        // 服务端治疗
        if (!player.level.isClientSide) {
            if (usedTicks == 40 || usedTicks == 80 || usedTicks == 120) {
                player.heal(10.0f);
            }
        }
    }

        @Override
        public UseAnim getUseAction (IToolStackView tool, ModifierEntry modifier){
            return UseAnim.BOW;
        }

        @Override
        public int getUseDuration (IToolStackView tool, ModifierEntry modifier){
            return 170;
        }

        @Override
        public void onFinishUsing (IToolStackView tool, ModifierEntry modifier, LivingEntity entity){
            if (!(entity instanceof Player player)) return;
            tool.setDamage(2147483647);
            if (player.level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Excalibur!!!").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            if (!player.level.isClientSide()) {
                destroyBlocksInSector(player,160,50,60);
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

        @Override
        protected void registerHooks (ModuleHookMap.Builder hookBuilder){
            hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
        }
    }
