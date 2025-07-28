package com.csdy.jzyy.entity.monster.ai;

import com.csdy.jzyy.entity.monster.entity.DogJiao;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;

public class DogJiaoMeleeGoal extends MeleeAttackGoal {
    private final DogJiao dogJiao; // 保存一个对我们实体本身的引用
    private int attackAnimationTicks; // 用于控制攻击动画状态持续时间的计时器

    public DogJiaoMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, DogJiao dogJiao) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.dogJiao = dogJiao;
    }

    private boolean isAttackingCurrently = false;

    @Override
    protected void checkAndPerformAttack(LivingEntity target,double squaredDistance) {
        if (this.mob.isWithinMeleeAttackRange(target)) {
            // 重置攻击冷却
            this.resetAttackCooldown();

            // 触发攻击动画！
            this.dogJiao.setAttacking(true);
            // 设置动画状态需要持续的tick数 (例如20 ticks = 1秒)
            // 这个值应该约等于你的攻击动画时长
            this.attackAnimationTicks = 40;

            // ... 你之前的伤害逻辑 ...
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
                this.dogJiao.setAttacking(false);
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
        // 我们希望攻击距离为 1 格。
        // 因为方法要求返回距离的平方，所以我们返回 1.0 * 1.0 = 1.0。
        // 为了更精确，可以考虑生物自身的碰撞箱宽度。
        // 例如，(1.0 + mob.getBbWidth()) * (1.0 + mob.getBbWidth())
        return 1.0D;
    }

    // 当AI行为停止时（例如目标死亡或超出范围），确保重置状态
    @Override
    public void stop() {
        super.stop();
        this.attackAnimationTicks = 0;
        this.dogJiao.setAttacking(false);
    }

    @Override
    public void start() {
        super.start();
        isAttackingCurrently = true; // 行为开始时，还未攻击
        // 可选: 通知Boss实体行为已开始
        // boss.setMovingState(true);
    }
}
