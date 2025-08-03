package com.csdy.jzyy.entity.boss.ai.mizi;

import com.csdy.jzyy.entity.boss.entity.MiziAo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;

import static com.csdy.jzyy.entity.boss.entity.MiziAo.RATING;

public class MiziMeleeGoal extends MeleeAttackGoal {
    private final MiziAo miziAo;
    // 重命名计时器，以更清晰地反映其作用：控制整个攻击序列
    private int attackSequenceTicks;

    public MiziMeleeGoal(MiziAo mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.miziAo = mob;
    }

    /**
     * 此方法现在仅用于“启动”攻击序列，而不是直接造成伤害。
     */
    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        double attackReachSqr = this.getAttackReachSqr(target);

        // 检查：是否在攻击距离内、攻击冷却已好、并且当前没有正在进行的攻击序列
        if (squaredDistance <= attackReachSqr && this.getTicksUntilNextAttack() <= 0 && this.attackSequenceTicks <= 0 && !miziAo.isDrinking()) {
            // --- 开始攻击序列 ---

            // 1. 重置攻击冷却，防止在动画期间再次触发此方法
            this.resetAttackCooldown();

            // 2. 开始播放动画
            this.miziAo.setAttacking(true);

            // 3. 设置整个攻击序列的持续时间（例如40 ticks = 2秒）
            this.attackSequenceTicks = 40;
        }
    }

    @Override
    public void tick() {
        super.tick(); // 必须调用，以保证Boss在蓄力时也能追击目标

        // 如果我们正处在一个攻击序列中...
        if (this.attackSequenceTicks > 0) {
            // 递减计时器
            this.attackSequenceTicks--;

            // --- 新增：在动画播放期间，周期性地破坏方块 ---
            // 定义破坏方块的间隔（例如每4个tick一次）
            int breakInterval = 4;
            if (this.attackSequenceTicks > 1 && this.attackSequenceTicks % breakInterval == 0) {
                // 调用实体自身的破坏方法
                this.miziAo.breakBlocksInFront();
            }
            // ---------------------------------------------

            // 在动画即将结束的瞬间 (例如，计时器为1时) 尝试造成伤害
            if (this.attackSequenceTicks == 1) {
                // (您已有的伤害逻辑保持不变)
                LivingEntity target = this.mob.getTarget();
                if (target != null) {
                    double distanceSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                    double reachSqr = this.getAttackReachSqr(target);
                    if (distanceSqr <= reachSqr) {
                        if (this.miziAo.isChujing()) {
                            if (target instanceof Player player && player.experienceLevel > RATING) target.hurt(this.miziAo.damageSources().mobAttack(this.miziAo), RATING);
                            else target.hurt(this.miziAo.damageSources().mobAttack(this.miziAo), RATING * 4);
                        } else {
                            this.miziAo.doHurtTarget(target);
                        }
                    }
                }
            }

            // 当计时器结束时，停止播放动画
            if (this.attackSequenceTicks <= 0) {
                this.miziAo.setAttacking(false);
            }
        }
    }

    /**
     * 当AI目标中断时（如目标死亡），必须重置所有状态。
     */
    @Override
    public void stop() {
        super.stop();
        this.attackSequenceTicks = 0;
        this.miziAo.setAttacking(false);
    }

    // getAttackReachSqr 方法保持不变
    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        if (this.miziAo.isChujing()) {
            return 6.0D * 6.0D;
        }
        return super.getAttackReachSqr(target);
    }
}
