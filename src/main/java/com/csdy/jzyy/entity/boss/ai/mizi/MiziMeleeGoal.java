package com.csdy.jzyy.entity.boss.ai.mizi;

import com.csdy.jzyy.entity.boss.entity.MiziAo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import static com.csdy.jzyy.entity.boss.entity.MiziAo.出警;

public class MiziMeleeGoal extends MeleeAttackGoal {
    private final MiziAo miziAo; // 保存一个对我们实体本身的引用
    private int attackAnimationTicks; // 用于控制攻击动画状态持续时间的计时器

    public MiziMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, MiziAo miziAo) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.miziAo = miziAo;
    }

    private boolean isAttackingCurrently = false;

    @Override
    protected void checkAndPerformAttack(LivingEntity target,double squaredDistance) {
        if (this.mob.isWithinMeleeAttackRange(target)) {
            this.resetAttackCooldown();

            this.miziAo.setAttacking(true);
            this.attackAnimationTicks = 40;

            boolean hasNoArmor = true;
            for (var armorPiece : target.getArmorSlots()) {
                if (!armorPiece.isEmpty()) {
                    hasNoArmor = false;
                    break;
                }
            }

            if (hasNoArmor) {
                target.hurt(this.mob.damageSources().mobAttack(this.mob), 4000.0F);
            } else {
                this.mob.doHurtTarget(target);
            }
        }
    }

    // 每个tick都会调用这个方法
    @Override
    public void tick() {
        super.tick();
        // 如果攻击动画正在播放，则递减计时器
        if (this.attackAnimationTicks > 0) {
            this.attackAnimationTicks--;
            // 当计时器结束，重置攻击动画状态
            if(this.attackAnimationTicks <= 0) {
                this.miziAo.setAttacking(false);
            }
        }
    }


    /**
     * 重写此方法来设定自定义的攻击距离。
     * 返回值是攻击距离的平方。
     * @param target 攻击的目标
     * @return 攻击距离的平方
     */
    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        if (出警) return (4.0 + mob.getBbWidth()) * (4.0 + mob.getBbWidth());
        return (1.0 + mob.getBbWidth()) * (1.0 + mob.getBbWidth());
    }

    // 当AI行为停止时（例如目标死亡或超出范围），确保重置状态
    @Override
    public void stop() {
        super.stop();
        this.attackAnimationTicks = 0;
        this.miziAo.setAttacking(false);
    }

    @Override
    public void start() {
        super.start();
        isAttackingCurrently = true; // 行为开始时，还未攻击
        // 可选: 通知Boss实体行为已开始
        // boss.setMovingState(true);
    }
}
