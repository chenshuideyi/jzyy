package com.csdy.jzyy.entity.boss.ai;

import com.csdy.jzyy.entity.boss.SwordManCsdy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CsdyMeleeGoal extends MeleeAttackGoal {
    private final SwordManCsdy boss; // 将 YourBossEntityClass 替换为你的Boss实体类名
    private boolean isAttackingCurrently = false; // 标记是否正在执行攻击动作

    public CsdyMeleeGoal(SwordManCsdy mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.boss = mob;
    }

    // 当攻击开始时调用 (不是每tick，是攻击动作的开始)
    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        super.checkAndPerformAttack(target, squaredDistance);
        // 在实际造成伤害的时刻附近，我们可以认为攻击正在发生
        // MeleeAttackGoal 内部的 attackTick 变量控制攻击计时
        if (this.getAttackReachSqr(target) >= squaredDistance) { // 确认在攻击范围内
            // this.resetAttackCooldown(); // super.checkAndPerformAttack 内部会调用
            // this.mob.swing(InteractionHand.MAIN_HAND); // super.checkAndPerformAttack 内部会调用
            target.invulnerableTime = 0;
            this.mob.doHurtTarget(target); // super.checkAndPerformAttack 内部会调用
            target.invulnerableTime = 0;
            this.mob.doHurtTarget(target);
            isAttackingCurrently = true; // 标记开始攻击动画
        }
    }

    @Override
    public void tick() {
        super.tick();
        // 如果攻击冷却结束（即可以再次攻击了），并且目标仍然有效，
        // 那么我们不再处于“正在攻击”的动画状态，可能回到“追击”
        if (this.getTicksUntilNextAttack() <= 0) { // MeleeAttackGoal 1.18+ (旧版可能是 attackTick)
            isAttackingCurrently = false;
        }
        // 如果目标丢失或者行为停止，也应该重置攻击状态
        if (this.mob.getTarget() == null || !this.canContinueToUse()) {
            isAttackingCurrently = false;
        }
    }

    @Override
    public void stop() {
        super.stop();
        isAttackingCurrently = false; // 行为停止时，重置攻击动画标记
        // 可选: 通知Boss实体行为已停止，以便在动画控制器中处理
        // boss.setAttackingState(false); // 如果你在Boss实体中有这样一个方法
    }

    @Override
    public void start() {
        super.start();
        isAttackingCurrently = false; // 行为开始时，还未攻击
        // 可选: 通知Boss实体行为已开始
        // boss.setMovingState(true);
    }

    // 公共方法供动画控制器查询
    public boolean isCurrentlyAttacking() {
        return isAttackingCurrently;
    }

    public boolean isChasing() {
        // 如果行为正在使用 (canContinueToUse)，并且没有正在攻击，那么可以认为是追击状态
        // 并且生物正在移动 (速度大于一个小阈值)
        return this.canContinueToUse() && !isAttackingCurrently && this.mob.getDeltaMovement().lengthSqr() > 0.01;
    }
}
