package com.csdy.jzyy.entity.monster.ai;

import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

public class HJMEntityGoal extends MeleeAttackGoal {
    private final HJMEntity hjmentity;
    private int attackAnimationTicks;

    public HJMEntityGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, HJMEntity hjmentity) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.hjmentity = hjmentity;

    }

    private boolean isAttackingCurrently = false;
    private final double customAttackRange = 1D;

    @Override
    protected double getAttackReachSqr(@NotNull LivingEntity target) {
        double engagementRange = customAttackRange + 5D;
        return engagementRange * engagementRange;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        if (this.mob.isWithinMeleeAttackRange(target)) {
            this.resetAttackCooldown();

            this.hjmentity.setAttacking(true);
            this.attackAnimationTicks = 40;
            String s = "extra_attack";
            if (this.mob.getPersistentData().getFloat(s) >= 4) {
                target.invulnerableTime = 0;
                target.hurt(LegacyDamageSource.mobAttack(this.mob).setBypassArmor().setBypassInvul().setBypassMagic().setBypassEnchantment().setBypassShield().setBypassInvulnerableTime(), 99);
            } else {
                this.mob.doHurtTarget(target);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackAnimationTicks > 0) {
            this.attackAnimationTicks--;
            if (this.attackAnimationTicks <= 0) {
                this.hjmentity.setAttacking(false);
            }
        }
    }


    @Override
    public void stop() {
        super.stop();
        this.attackAnimationTicks = 0;
        this.hjmentity.setAttacking(false);
    }

    @Override
    public void start() {
        super.start();
        isAttackingCurrently = true;
    }


}