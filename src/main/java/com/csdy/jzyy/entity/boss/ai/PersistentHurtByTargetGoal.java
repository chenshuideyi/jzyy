package com.csdy.jzyy.entity.boss.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class PersistentHurtByTargetGoal extends HurtByTargetGoal {
    public PersistentHurtByTargetGoal(PathfinderMob mob) {
        super(mob);
    }

    @Override
    public boolean canContinueToUse() {
        // 即使原始仇恨目标消失也继续寻找新目标
        if (this.mob.getTarget() == null || !this.mob.getTarget().isAlive()) {
            this.findNewTarget();
            return true;
        }
        return super.canContinueToUse();
    }

    private void findNewTarget() {
        // 寻找最近的可攻击目标
        LivingEntity newTarget = this.mob.level()
                .getNearestEntity(LivingEntity.class, TargetingConditions.forCombat(),
                        this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                        this.mob.getBoundingBox().inflate(16.0D));

        if (newTarget != null) {
            this.mob.setTarget(newTarget);
        }
    }
}
