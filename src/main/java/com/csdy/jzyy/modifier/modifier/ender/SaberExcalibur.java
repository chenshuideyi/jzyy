package com.csdy.jzyy.modifier.modifier.ender;

import com.csdy.jzyy.particle.register.JzyyParticlesRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SaberExcalibur extends NoLevelsModifier implements GeneralInteractionModifierHook {

    SimpleParticleType type = JzyyParticlesRegister.SABER_PARTICLE.get();

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

        if (player.level().isClientSide()) {
            RandomSource random = player.getRandom();
            for (int i = 0; i < 40; i++) { // 减少粒子数量但增强动态效果
                // 1. 更柔和的初始位置分布（减少机械感）
                float radius = 1.5f + random.nextFloat() * 5f; // 半径随机化
                float angle = random.nextFloat() * Mth.TWO_PI;  // 360度随机角度
                float x = Mth.cos(angle) * radius;
                float z = Mth.sin(angle) * radius;

                // 2. 飘逸运动参数
                float speedX = (random.nextFloat() - 0.5f) * 0.02f; // 更小的水平速度
                float speedY = 0.1f + random.nextFloat() * 0.3f;    // 缓慢上升
                float speedZ = (random.nextFloat() - 0.5f) * 0.02f;

                // 3. 添加粒子（使用自定义粒子类型或原版粒子）
                player.level().addParticle(
                        type, // 或你的自定义粒子
                        player.getX() + x,
                        player.getY() + 0.5,   // 从腰部高度发射
                        player.getZ() + z,
                        speedX,
                        speedY,
                        speedZ
                );
            }
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
                destroyBlocksInFront(player, 100, 50, 30);
            }
        }

    private void destroyBlocksInFront(Player player, int length, int height, int width) {
        Level level = player.level;

        // 获取玩家朝向的方向向量
        Vec3 lookAngle = player.getLookAngle();

        // 计算破坏区域的中心点（玩家前方 `length/2` 处）
        BlockPos center = player.blockPosition()
                .offset(
                        (int) (lookAngle.x * length / 2),
                        (int) (lookAngle.y * length / 2),
                        (int) (lookAngle.z * length / 2)
                );

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        // 遍历区域内的所有方块
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = -halfHeight; y <= halfHeight; y++) {
                for (int z = 0; z < length; z++) {
                    // 计算当前方块的坐标（基于朝向调整）
                    BlockPos targetPos = center
                            .offset(
                                    (int) (lookAngle.x * z - lookAngle.z * x),
                                    y,
                                    (int) (lookAngle.z * z + lookAngle.x * x)
                            );

                    // 破坏方块（空气替换）
                    level.destroyBlock(targetPos, false, player);
                }
            }
        }
    }

        @Override
        protected void registerHooks (ModuleHookMap.Builder hookBuilder){
            hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
        }
    }
