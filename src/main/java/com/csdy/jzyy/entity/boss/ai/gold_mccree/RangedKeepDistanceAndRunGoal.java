package com.csdy.jzyy.entity.boss.ai.gold_mccree;

import com.csdy.jzyy.entity.boss.entity.GoldMcCree;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionSeverance;

public class RangedKeepDistanceAndRunGoal extends KeepDistanceGoal {

    private int attackTime = -1;
    private final int attackInterval;
    private final float attackRadius;
    private Vec3 lastMovePos;

    // 添加移动状态控制
    private boolean shouldMove = false;
    private int moveCooldown = 0;

    public RangedKeepDistanceAndRunGoal(GoldMcCree mob, double speedModifier,
                                        float minDistance, float maxDistance,
                                        int attackInterval, float attackRadius) {
        super(mob, speedModifier, minDistance, maxDistance);
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
    }

    @Override
    public void tick() {

        if (this.mob.getPhase() == 1) return;

        if (this.target == null) {
            this.target = this.mob.getTarget();
            if (this.target == null) return;
        }

        // 完全停止导航系统，我们自己控制移动
        this.mob.getNavigation().stop();

        // 强制面向目标
        forceLookAtTarget();

        // 自定义移动逻辑
        updateCustomMovement();

        // 攻击逻辑
        updateAttack();

        System.out.println("执行边跑边射 - 移动: " + shouldMove + " | 攻击冷却: " + attackTime);
    }

    private void updateCustomMovement() {
        float currentDistance = this.mob.distanceTo(this.target);

        // 移动冷却
        if (moveCooldown > 0) {
            moveCooldown--;
            if (moveCooldown <= 0) {
                shouldMove = false;
            }
        }

        // 检查距离是否需要开始新的移动周期
        if ((currentDistance < minDistance || currentDistance > maxDistance) && !shouldMove && moveCooldown <= 0) {
            shouldMove = true;
            moveCooldown = 70; // 移动2.5秒 + 冷却2秒
            this.mob.setMovingWithDuration(true);
        }

        // 执行移动
        if (shouldMove) {
            Vec3 movePos = findSuitablePosition();
            if (movePos != null) {
                // 手动移动，不通过导航系统
                Vec3 moveDir = movePos.subtract(this.mob.position()).normalize();

                // 设置移动速度
                double moveSpeed = this.speedModifier * 0.3;
                this.mob.setDeltaMovement(
                        moveDir.x * moveSpeed,
                        this.mob.getDeltaMovement().y,
                        moveDir.z * moveSpeed
                );

                lastMovePos = movePos;
            }
        } else {
            // 停止移动
            this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
        }

        // 应用移动
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
    }

    private void updateAttack() {
        float distance = this.mob.distanceTo(this.target);
        boolean inRange = distance <= this.attackRadius;
        boolean canSee = this.mob.getSensing().hasLineOfSight(this.target);

        if (canSee && inRange && --this.attackTime <= 0) {
            performRangedAttack();
            shoot(target);
            this.attackTime = this.attackInterval;
        }
    }

    private void forceLookAtTarget() {
        if (this.target == null) return;

        // 计算到目标的方向
        double dx = this.target.getX() - this.mob.getX();
        double dz = this.target.getZ() - this.mob.getZ();

        // 计算目标朝向（角度）
        float targetYRot = (float)(Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;

        // 直接设置所有朝向相关变量
        this.mob.setYRot(targetYRot);
        this.mob.setYBodyRot(targetYRot);
        this.mob.setYHeadRot(targetYRot);
        this.mob.yBodyRot = targetYRot;
        this.mob.yHeadRot = targetYRot;
        this.mob.yRotO = targetYRot;

        // 同时设置 LookControl
        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
    }

    private void performRangedAttack() {
        // 使用持续时间方法设置攻击状态
        this.mob.setAttackingWithDuration(true);
        System.out.println("开始射击 - 持续2.5秒");
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAttacking(false);
        this.mob.setMovingWithDuration(false);
        forceLookAtTarget();
        // 停止移动
        this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
        System.out.println("停止边跑边射");
        this.attackTime = -1;
        this.lastMovePos = null;
        this.shouldMove = false;
        this.moveCooldown = 0;
    }

    @Override
    public void start() {
        this.mob.setAttacking(true);
        this.target = this.mob.getTarget();
        forceLookAtTarget();
        this.mob.getNavigation().stop();
        System.out.println("开始边跑边射模式");
    }

    private void shoot(LivingEntity target){
        for (int i = 0; i<6; i++) {
            reflectionSeverance(target, target.getHealth() - 158);
        }
    }
}