package com.csdy.jzyy.entity.boss.ai;

import com.csdy.jzyy.entity.boss.entity.SwordManCsdy;
import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromDummmmmmyMod;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceSetAllCandidateHealth;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.setAbsoluteSeveranceHealth;

public class CsdyMeleeGoal extends MeleeAttackGoal {

    @Getter
    private final SwordManCsdy boss;
    private boolean isAttackingCurrently = false; // 标记是否正在执行攻击动作，用于动画控制

    public CsdyMeleeGoal(SwordManCsdy mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.boss = mob;
    }

    /**
     * Goal开始执行。
     */
    @Override
    public void start() {
        super.start();
        // Goal开始时，重置攻击状态
        this.isAttackingCurrently = false;
        // 你可以在这里通知Boss实体，让它进入“追击”的动画状态
         this.boss.setAttacking(true);
    }

    /**
     * Goal停止执行。
     */
    @Override
    public void stop() {
        super.stop();
        // Goal结束时，确保重置攻击状态
        this.isAttackingCurrently = false;
        // 你可以在这里通知Boss实体，攻击已结束，返回待机状态
    }

    /**
     * 每个tick都会执行的逻辑。
     * super.tick() 中包含了移动向目标的核心逻辑。
     */
    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            // 当我们足够接近目标时，检查是否可以攻击
            // this.getTicksUntilNextAttack() <= 0 表示攻击冷却已经结束
            if (this.mob.distanceToSqr(target) <= this.getAttackReachSqr(target) && this.getTicksUntilNextAttack() <= 0) {
                // 标记我们正处于攻击动画状态
                this.isAttackingCurrently = true;

            }
        }
    }

    /**
     * 核心方法：检查并执行攻击。
     * 这个方法在 mob 靠近目标且攻击冷却结束后被 super.tick() 调用。
     */
    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        double attackRangeSqr = this.getAttackReachSqr(target);
        if (squaredDistance <= attackRangeSqr) {  // 移除冷却检查
            this.resetAttackCooldown();  // 仍然调用以重置动画计时（可选）
            this.mob.getNavigation().stop();
            performFourHitCombo(target);
            this.isAttackingCurrently = false;
        }
    }

    // 覆盖父类方法，始终返回 true 以禁用冷却
    @Override
    protected boolean isTimeToAttack() {
        return true;
    }

    /**
     * 封装的四连击与范围伤害逻辑。
     * @param mainTarget 主要攻击的目标
     */
    private void performFourHitCombo(LivingEntity mainTarget) {
        // --- 连击参数 ---
        final int hitCount = 4;      // 连击次数
        final double aoeRadius = 4.0D; // 范围伤害的半径

        for (int i = 0; i < hitCount; i++) {
            // 每次攻击前都检查主目标是否还活着
            if (!mainTarget.isAlive()) {
                break; // 如果主目标死亡，立即停止连击
            }

            Vec3 dashDirection = new Vec3(
                    mainTarget.getX() - this.mob.getX(),
                    0,  // Y轴通常不移动，避免浮空
                    mainTarget.getZ() - this.mob.getZ()
            ).normalize().scale(4);

            this.mob.setDeltaMovement(
                    dashDirection.x,
                    this.mob.getDeltaMovement().y,  // 保留原有Y轴速度（重力）
                    dashDirection.z
            );

            // --- A. 攻击主要目标 ---
            dealDamageToTarget(mainTarget);

            // --- B. 对主要目标附近的敌人进行范围攻击 ---
            AABB aoeBox = this.mob.getBoundingBox().inflate(aoeRadius);
            List<LivingEntity> nearbyEntities = this.mob.level.getEntitiesOfClass(LivingEntity.class, aoeBox,
                    entity -> entity.isAlive() &&        // 必须存活
                            entity != this.mob &&       // 不能是自己
                            entity != mainTarget &&     // 不能是已经被打过的主目标
                            !this.mob.isAlliedTo(entity) && // 不能是盟友
                            !(entity instanceof Player && ((Player) entity).isCreative()) // 不能是创造模式玩家
            );

            for (LivingEntity nearbyTarget : nearbyEntities) {
                dealDamageToTarget(nearbyTarget); // 对范围内的其他目标造成伤害
            }
        }
    }

    /**
     * 对单个目标造成伤害，并包含你的特殊逻辑。
     * @param target 要伤害的目标
     */
    private void dealDamageToTarget(LivingEntity target) {
        if (!target.isAlive()) return; // 安全检查
        target.invulnerableTime = 0;      // 强制取消无敌帧，以实现快速连击
        this.mob.doHurtTarget(target); // 造成常规伤害

//        if (boss.isReal()){
//            setAbsoluteSeveranceHealth(target, 0);
//            forceSetAllCandidateHealth(target, 0);
//        }


        if (!(target instanceof Player) && !(isFromDummmmmmyMod(target))) {
            float oldHealth = target.getHealth();
            float reHealth = target.getHealth() - target.getMaxHealth() * 0.01f;
            setAbsoluteSeveranceHealth(target, reHealth);
            forceSetAllCandidateHealth(target, reHealth);
            if (target.getHealth() >= oldHealth || target.getHealth() > reHealth || target.getHealth() <= 0) {
            }
        }
    }

    /**
     * 让AI知道我们的攻击距离，以便它在正确的位置停下并准备攻击。
     */
    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        // 自定义一个更近的攻击触发距离，原版MeleeAttackGoal的攻击距离判定比较远
        double customAttackRange = 4.0D;
        return customAttackRange * customAttackRange;
    }

    /**
     * 你可以添加一个公共方法，供实体动画控制器调用。
     * @return Boss当前是否正在执行攻击动作。
     */
    public boolean isAttacking() {
        return this.isAttackingCurrently;
    }

}
