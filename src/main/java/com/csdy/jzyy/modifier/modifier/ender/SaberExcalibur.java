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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Random;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionPenetratingDamage;

public class SaberExcalibur extends NoLevelsModifier implements GeneralInteractionModifierHook, InventoryTickModifierHook {

    private static final int radius = 160; // 破坏半径
    private static final float angle = 50; // 角度
    private static final int height = 60;  // 高度

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
            if (usedTicks == 80) {
                player.displayClientMessage(
                        Component.literal("汇聚的星之吐息").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            else if (usedTicks == 120) {
                player.displayClientMessage(
                        Component.literal("闪耀的生命奔流").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            else if (usedTicks == 165) {
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
        public void onFinishUsing (IToolStackView tool, ModifierEntry entry, LivingEntity entity){
            if (!(entity instanceof Player player)) return;
            float baseDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            float damage = baseDamage;

            ToolAttackContext context = new ToolAttackContext(player, player, InteractionHand.MAIN_HAND,
                    player, player, false, 1.0f, false);

            for (ModifierEntry modifierEntry : tool.getModifierList()) {
                entry.getHook(ModifierHooks.MELEE_DAMAGE);
                damage = entry.getHook(ModifierHooks.MELEE_DAMAGE)
                        .getMeleeDamage(tool, modifierEntry, context, baseDamage, damage);
            }

            damage = damage * 50;

            if (player.level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Excalibur!!!").withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            if (!player.level.isClientSide()) {
                destroyBlocksInSector(player, radius, angle, height);
                damageEntitiesInSector(player, radius, angle, height,damage);
            }
            tool.setDamage(2147483647);
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
        Level level = player.level; // 在Minecraft 1.19.x+ 中是 player.level(), 老版本可能是 player.level
        Random random = new Random(); // 用于随机破坏边缘方块

        Vec3 playerPos = player.position();
        Vec3 lookAngle = player.getLookAngle().normalize(); // 视线向量，确保已归一化

        float halfAngle = angleDegrees / 2;
        double angleRad = Math.toRadians(halfAngle);
        double cosThreshold = Math.cos(angleRad); // 角度阈值的余弦值

        BlockPos playerBlockPos = player.blockPosition(); // 玩家脚下的方块位置

        // 缓存玩家精确位置的XZ坐标，用于优化向量计算
        double playerExactX = playerPos.x;
        double playerExactZ = playerPos.z;

        for (int yOffset = -height; yOffset <= height; yOffset++) {
            // 根据高度动态调整当前扫描半径，形成锥形效果
            double normalizedY = Math.abs(yOffset) / (double) height;
            double currentRadius = radius * (1 - normalizedY); // 线性递减半径
            int currentRadiusInt = (int) Math.ceil(currentRadius);

            if (currentRadiusInt <= 0) continue; // 如果当前半径为0或负数，跳过该平面

            for (int xOffset = -currentRadiusInt; xOffset <= currentRadiusInt; xOffset++) {
                for (int zOffset = -currentRadiusInt; zOffset <= currentRadiusInt; zOffset++) {
                    // 计算当前方块中心到该平面圆心的距离的平方
                    double distanceSqXZ = xOffset * xOffset + zOffset * zOffset;
                    double maxDistanceSqXZ = currentRadius * currentRadius;

                    // 1. 快速圆形范围检查 (在XZ平面上)
                    if (distanceSqXZ > maxDistanceSqXZ) {
                        continue;
                    }

                    // 计算从玩家精确位置指向目标方块中心（XZ平面）的未归一化向量 (dx, dz)
                    // 目标方块中心X: playerBlockPos.getX() + xOffset + 0.5
                    // 目标方块中心Z: playerBlockPos.getZ() + zOffset + 0.5
                    double targetBlockCenterX = playerBlockPos.getX() + xOffset + 0.5;
                    double targetBlockCenterZ = playerBlockPos.getZ() + zOffset + 0.5;

                    double dx = targetBlockCenterX - playerExactX;
                    double dz = targetBlockCenterZ - playerExactZ;

                    // 如果dx和dz都为0，意味着目标方块中心与玩家精确位置在XZ平面上重合
                    // 此时方向不确定，可以跳过，或者视为在正前方 (取决于具体需求)
                    // 为简单起见，如果长度为0，点积将为0，可能导致不正确的行为，所以最好检查
                    if (dx == 0 && dz == 0) {
                        // 这个方块就在玩家脚下XZ投影点，可以特殊处理或直接包含
                        // 假设总是包含在视线内（如果需要，否则continue）
                    }

                    // 2. 扇形区域检查 (优化点积计算)
                    // A · B = |A| |B| cos(theta)
                    // lookAngle · toBlock_unnormalized >= cosThreshold * |toBlock_unnormalized|
                    // (lookAngle.x * dx + lookAngle.z * dz) >= cosThreshold * sqrt(dx*dx + dz*dz)
                    // 为了避免开方，两边平方：(dot_product_unnormalized)^2 >= (cosThreshold * length_unnormalized)^2
                    // 注意：如果cosThreshold可能为负（即角度大于90度），平方会导致问题。
                    // 但在此处 angleDegrees 通常是 (0, 180]，所以 halfAngle 是 (0, 90]，cosThreshold 是 [0, 1)。
                    // 所以平方是安全的。

                    double dotProductUnnormalized = lookAngle.x * dx + lookAngle.z * dz;
                    double lengthSqToBlockUnnormalized = dx * dx + dz * dz; // dx^2 + dz^2

                    // 必须确保lengthSqToBlockUnnormalized不为0，否则下面会除以0或sqrt(0)
                    if (lengthSqToBlockUnnormalized == 0) { // 玩家与目标方块中心在XZ上重合
                        if (cosThreshold <= 0) { // 如果视角大于等于180度，则认为在扇区内
                            // 这种情况通常不会发生，因为halfAngle <= 90
                        } else {
                            // 认为在扇区内，或者根据具体需求处理
                        }
                    } else {
                        // 原始比较：dotProductNormalized >= cosThreshold
                        // (dotProductUnnormalized / Math.sqrt(lengthSqToBlockUnnormalized)) >= cosThreshold
                        // 避免开方：
                        // 如果 dotProductUnnormalized < 0 且 cosThreshold > 0，则肯定不满足，因为左边负右边正
                        // 如果 dotProductUnnormalized >= 0：
                        //    两边平方：dotProductUnnormalized^2 / lengthSqToBlockUnnormalized >= cosThreshold^2
                        //    dotProductUnnormalized^2 >= cosThreshold^2 * lengthSqToBlockUnnormalized
                        if (dotProductUnnormalized < 0 && cosThreshold > 0) { // 目标在身后，且要求在前方
                            continue;
                        }
                        if (dotProductUnnormalized * dotProductUnnormalized < cosThreshold * cosThreshold * lengthSqToBlockUnnormalized) {
                            continue;
                        }
                        // 对于cosThreshold < 0 的情况（即角度大于180度），上述平方逻辑需要小心。
                        // 但通常扇形角度 < 180度，所以 cosThreshold >= 0。
                        // 更简单且原始的判断（如果上面的平方判断让你困惑）：
                        // if (dotProductUnnormalized < cosThreshold * Math.sqrt(lengthSqToBlockUnnormalized)) {
                        //    continue;
                        // }
                        // 这个版本更直接，但有一次开方。上面的平方版本避免了开方。
                    }


                    // 如果通过了所有检查，获取方块位置并尝试破坏
                    BlockPos targetPos = playerBlockPos.offset(xOffset, yOffset, zOffset);
                    BlockState state = level.getBlockState(targetPos);

                    // 3. 检查方块是否可被替换（固体可破坏或液体）
                    boolean isAir = state.isAir();
                    // 检查是否为液体，具体方法可能因Minecraft版本和Modding API而异
                    // Forge: state.getMaterial().isLiquid()
                    // Fabric: state.isLiquid() (较新版本) 或者 state.getFluidState().isSource() / !state.getFluidState().isEmpty()
                    // 这里用一个通用的假设，你需要替换成你环境中的实际API
                    boolean isLiquid = state.getFluidState().isSource(); // 示例：检查是否为源液体方块
                    // 或者 state.getMaterial().isLiquid();

                    // 可破坏的固体条件：挖掘速度>=0 且 不是空气
                    boolean isBreakableSolid = state.getDestroySpeed(level, targetPos) >= 0 && !isAir;


                    if (!isAir && (isBreakableSolid || isLiquid)) {
                        // 判断是否在最外层（距离 >= 当前半径的 90%），用于随机破坏边缘
                        // distanceSqXZ 是到当前XZ平面圆心的距离平方
                        boolean isOuterLayer = distanceSqXZ >= (0.9 * 0.9 * maxDistanceSqXZ); // 0.9^2 = 0.81

                        if (!isOuterLayer) {
                            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3); // 3 = UPDATE_ALL_IMMEDIATE
                        } else {
                            // 外层方块：50% 概率破坏（模拟不规则边缘）
                            if (random.nextFloat() < 0.5f) {
                                level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                            }
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

            double dotProduct = lookAngle.x * toEntity.x + lookAngle.z * toEntity.z;
            if (dotProduct >= cosThreshold) {
                double verticalRatio = (target.getY() - playerPos.y) / height;
                if (Math.abs(verticalRatio) <= 1.0) {
                    double distance = playerPos.distanceTo(target.position());
                    if (distance <= radius) {
                        reflectionPenetratingDamage(target,player,damage);
                    }
                }
            }
        }
    }

        @Override
        protected void registerHooks (ModuleHookMap.Builder hookBuilder){
            hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
            hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        }


}
