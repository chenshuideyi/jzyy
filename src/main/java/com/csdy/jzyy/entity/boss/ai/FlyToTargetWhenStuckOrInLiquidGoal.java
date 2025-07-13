package com.csdy.jzyy.entity.boss.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class FlyToTargetWhenStuckOrInLiquidGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private LivingEntity targetEntity;
    private int checkInterval = 20; // 每隔多少tick检查一次条件和目标
    private int timeSinceLastCheck = 0;
    private double flightHeightOffset = 2.0; // 尝试飞到目标上方一点的高度
    private double maxFlyDistanceSq = 64.0 * 64.0; // 最大飞行距离的平方 (64格)
    private double minTriggerDistanceSq = 2.0 * 2.0; // 离目标太近就不需要飞了

    private static final int MAX_STUCK_CHECKS = 5; // 检查周围多少个点来判断是否被堵住
    private static final double STUCK_RADIUS = 1.0; // 检查堵塞的半径

    public FlyToTargetWhenStuckOrInLiquidGoal(PathfinderMob mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); // 需要移动和朝向控制
    }

    @Override
    public boolean canUse() {
        if (timeSinceLastCheck < checkInterval) {
            timeSinceLastCheck++;
            return false;
        }
        timeSinceLastCheck = 0;

        // 1. 条件检测
        boolean isInLiquid = this.mob.isInWaterOrBubble() || this.mob.isInLava() || this.mob.isEyeInFluid(FluidTags.WATER) || this.mob.isEyeInFluid(FluidTags.LAVA);
        boolean isStuck = !isInLiquid && isEffectivelyStuck(); // 如果在液体里，优先处理液体情况，不判断stuck

        if (!isInLiquid && !isStuck) {
            return false; // 条件不满足
        }

        // 2. 目标选择
        this.targetEntity = findNearestTargetableEntity();
        if (this.targetEntity == null) {
            return false; // 没有找到目标
        }

        // 如果目标太远或太近，可能不适合飞行
        double distanceSqToTarget = this.mob.distanceToSqr(this.targetEntity);
        if (distanceSqToTarget > maxFlyDistanceSq || distanceSqToTarget < minTriggerDistanceSq) {
            this.targetEntity = null;
            return false;
        }

         if (this.mob.getY() > this.targetEntity.getY() + flightHeightOffset - 1.0 &&
             this.mob.distanceToSqr(this.targetEntity.getX(), this.targetEntity.getY() + flightHeightOffset, this.targetEntity.getZ()) < 4.0) {
             return false;
         }

        return true;
    }

    private boolean isEffectivelyStuck() {
        // 简单的卡住检测：如果导航长时间无法前进，或者周围被方块包围
        if (this.mob.getNavigation().isStuck() && this.mob.getNavigation().getMaxDistanceToWaypoint() > 60) {
            return true;
        }

        // 检查周围是否被固体方块包围 (简化版)
        // 可以在水平面和略高于头顶的位置检查
        Level level = this.mob.level();
        BlockPos mobPos = this.mob.blockPosition();
        int solidBlocksAround = 0;
        int checksDone = 0;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue; // 跳过脚下
                for (int y = 0; y <= 1; y++) { // 脚下平面和头顶平面
                    if (checksDone >= MAX_STUCK_CHECKS) break;
                    BlockPos checkPos = mobPos.offset(x, y, z);
                    if (level.getBlockState(checkPos).isRedstoneConductor(level, checkPos)) { // isSolidRender / isSuffocating
                        solidBlocksAround++;
                    }
                    checksDone++;
                }
                if (checksDone >= MAX_STUCK_CHECKS) break;
            }
            if (checksDone >= MAX_STUCK_CHECKS) break;
        }
        // 如果周围超过一定数量的检测点是固体方块，则认为被堵住
        return solidBlocksAround >= (MAX_STUCK_CHECKS / 2 + 1); // 例如，超过一半的点是固体
    }


    private LivingEntity findNearestTargetableEntity() {
        Level level = this.mob.level();
        // 优先寻找玩家
        Player nearestPlayer = level.getNearestPlayer(this.mob, maxFlyDistanceSq); // getNearestPlayer(targetEntity, maxDistance)

        if (nearestPlayer != null && this.mob.getSensing().hasLineOfSight(nearestPlayer) && nearestPlayer.distanceToSqr(this.mob) < maxFlyDistanceSq) {
            // 可以在这里添加额外的目标有效性检查 (例如，不是创造模式的玩家)
            if (!nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                return nearestPlayer;
            }
        }

        // 如果没有合适的玩家，寻找其他生物 (可以根据需要过滤类型)
        // 这里以最近的LivingEntity为例，你可以用targetSelector的逻辑或自定义列表
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
                LivingEntity.class,
                this.mob.getBoundingBox().inflate(Math.sqrt(maxFlyDistanceSq), 8.0, Math.sqrt(maxFlyDistanceSq)), // 搜索范围
                (entity) -> entity != this.mob && entity.isAlive() && !(entity instanceof Player) && this.mob.getSensing().hasLineOfSight(entity)
        );

        if (nearbyEntities.isEmpty()) {
            return null;
        }

        // 返回最近的非玩家生物
        return nearbyEntities.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this.mob)))
                .orElse(null);
    }


    @Override
    public boolean canContinueToUse() {
        if (this.targetEntity == null || !this.targetEntity.isAlive()) {
            return false;
        }
        // 如果离目标很近了，或者目标太远，或者生物不再被困/在液体中，则停止
        double distanceSqToTarget = this.mob.distanceToSqr(this.targetEntity);
        if (distanceSqToTarget < minTriggerDistanceSq || distanceSqToTarget > maxFlyDistanceSq) {
            return false;
        }

        // 重新检查条件
        boolean isInLiquid = this.mob.isInWaterOrBubble() || this.mob.isInLava() || this.mob.isEyeInFluid(FluidTags.WATER) || this.mob.isEyeInFluid(FluidTags.LAVA);
        boolean isStuck = !isInLiquid && isEffectivelyStuck();
        if (!isInLiquid && !isStuck) {
            return false;
        }

        return true; // 否则继续飞行
    }

    @Override
    public void start() {
        // 飞行开始时的准备工作
        // 停止地面导航，以防冲突
        this.mob.getNavigation().stop();
        // 设置朝向目标
        this.mob.getLookControl().setLookAt(this.targetEntity, 30.0F, 30.0F);
    }

    @Override
    public void tick() {
        if (this.targetEntity == null) return;

        // 持续朝向目标
        this.mob.getLookControl().setLookAt(this.targetEntity, 30.0F, 30.0F);

        // 计算飞向目标位置 (目标实体上方一点)
        Vec3 targetFlyPos = new Vec3(
                this.targetEntity.getX(),
                this.targetEntity.getY() + flightHeightOffset, // 飞到目标头顶附近
                this.targetEntity.getZ()
        );

        // 如果已经在目标位置附近，则悬停或进行下一步动作（例如俯冲攻击，这需要另一个Goal）
        if (this.mob.position().distanceToSqr(targetFlyPos) < 2.0 * 2.0) { // 到达目标点附近
            // 可以在这里减速或悬停
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.8)); // 简单减速
            // 如果这是一个准备攻击的飞行，可以在这里转换到攻击Goal
            // 或者让这个Goal自己处理攻击（但不推荐，最好分离行为）
            return;
        }

        // 简单的飞行逻辑：直接朝目标点移动
        // 你可能需要更复杂的飞行控制器来处理碰撞、平滑移动等
        Vec3 directionToTarget = targetFlyPos.subtract(this.mob.position()).normalize();
        Vec3 desiredVelocity = directionToTarget.scale(this.speedModifier);

        // 应用速度 (这里只是简单设置，真实飞行生物会有更复杂的加速度和最大速度控制)
        this.mob.setDeltaMovement(desiredVelocity);

        // 确保生物不会掉下去 (如果它是基于重力的生物)
        // 对于飞行生物，这可能不是必需的，或者有专门的飞行控制器
        if (!this.mob.onGround() && this.mob.getDeltaMovement().y < 0.0D && !this.mob.level().getBlockState(this.mob.blockPosition().below()).isAir()) {
            // 如果在空中且向下掉，且脚下不是空气（避免卡入地面），可以稍微给点向上的力
            // this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0, 0.05, 0));
        }
        // 对于非飞行生物，你需要确保它能克服重力
        // 例如，不断施加一个向上的力，或者直接修改Y轴位置（不推荐，会不自然）
        // 如果mob不是天然飞行生物，你可能需要给它一个短暂的无重力效果或持续的向上推力
        // this.mob.setNoGravity(true); // 效果期间无重力 (记得在stop()中设回false)
    }

    @Override
    public void stop() {
        this.targetEntity = null;
        // this.mob.setNoGravity(false); // 如果在start()或tick()中设置了无重力，在这里恢复
        // 如果飞行行为停止，可以尝试恢复地面导航
        // this.mob.getNavigation().recomputePath(); // 尝试重新计算路径
    }
}
