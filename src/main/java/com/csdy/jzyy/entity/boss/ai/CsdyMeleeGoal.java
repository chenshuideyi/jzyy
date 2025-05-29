package com.csdy.jzyy.entity.boss.ai;

import com.csdy.jzyy.entity.boss.SwordManCsdy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CsdyMeleeGoal extends MeleeAttackGoal {
    private final SwordManCsdy boss; // 将 YourBossEntityClass 替换为你的Boss实体类名
    private boolean isAttackingCurrently = false; // 标记是否正在执行攻击动作

    public CsdyMeleeGoal(SwordManCsdy mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.boss = mob;
    }

    private final double customAttackRange = 2D; // 例如，设置为1.5格。请根据需要调整。

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        // 计算我们自定义的攻击距离的平方值
        // 你也可以更精确地考虑目标宽度，但为了简单起见，我们先用固定距离。
        // 例如: double effectiveRange = this.customAttackRange + target.getBbWidth() / 2.0;
        // double desiredAttackReachSqr = effectiveRange * effectiveRange;
        double desiredAttackReachSqr = this.customAttackRange * this.customAttackRange;

        // 检查是否在自定义的更近攻击范围内，并且攻击冷却已过
        // this.isTimeToAttack() 是 MeleeAttackGoal 中检查 attackTick <= 0 的方法
        if (squaredDistance <= desiredAttackReachSqr && this.isTimeToAttack()) {
            // 重置攻击冷却计时器
            this.resetAttackCooldown(); // 这个方法来自 MeleeAttackGoal

            // --- 四连击逻辑 ---
            int hits = 4;
            double aoeRadius = 2D; // 这是范围伤害的半径，与攻击触发距离是不同的概念

            for (int i = 0; i < hits; i++) {
                // --- 攻击主要目标 ---
                if (target.isAlive()) { // 确保目标在连击过程中仍然存活
                    target.invulnerableTime = 0; // 尝试取消无敌时间
                    this.mob.doHurtTarget(target); // 对主要目标造成伤害
                } else {
                    break; // 如果主要目标死亡，则停止连击
                }

                // --- 对主要目标附近的生物进行范围攻击 ---
                AABB aoeBox = this.mob.getBoundingBox().inflate(aoeRadius);
                List<LivingEntity> nearbyEntities = this.mob.level.getEntitiesOfClass(LivingEntity.class, aoeBox,
                        nearbyEntity -> nearbyEntity.isAlive() &&        // 必须存活
                                nearbyEntity != this.mob &&       // 不能是攻击者自己
                                nearbyEntity != target &&         // 不能是已经单独处理过的主目标
                                this.mob.canAttack(nearbyEntity) && // 使用mob的canAttack进行通用可攻击性检查
                                !nearbyEntity.isAlliedTo(this.mob) && // 再次确认非同盟
                                !(nearbyEntity instanceof Player && ((Player) nearbyEntity).getAbilities().invulnerable) // 非创造/无敌玩家
                );

                for (LivingEntity nearbyTarget : nearbyEntities) {
                    if (nearbyTarget.isAlive()) { // 再次检查存活状态
                        nearbyTarget.invulnerableTime = 0; // 尝试取消无敌时间
                        this.mob.doHurtTarget(nearbyTarget); // 对范围内的目标造成伤害
                    }
                }
            }
            // 如果你有 isAttackingCurrently 这样的标记，可以在这里设置
            // isAttackingCurrently = true;
        }
        // 注意：我们不再调用 super.checkAndPerformAttack(target, squaredDistance);
        // 因为我们用自定义的逻辑和攻击距离完全替换了它。
    }
    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        // 返回与 checkAndPerformAttack 中使用的自定义攻击距离的平方值
        // 这样，生物会在进入 customAttackRange 范围时停止移动并尝试攻击。
        // 如果希望索敌/停止移动的距离比实际攻击触发距离稍大一点，可以这样做：
        // double engagementRange = this.customAttackRange + 0.5D; // 例如，索敌距离比攻击距离大0.5格
        // return engagementRange * engagementRange;

        // 如果希望索敌和攻击触发距离完全一致：
        return this.customAttackRange * this.customAttackRange;

        // 如果想更精确地考虑目标宽度 (这会使得对不同大小的目标有不同的绝对距离):
        // double effectiveRange = this.customAttackRange + target.getBbWidth() / 2.0D;
        // return effectiveRange * effectiveRange;
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
