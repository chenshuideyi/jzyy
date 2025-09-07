package com.csdy.jzyy.entity.boss.ai.dog_jiao_jiao_jiao;

import com.csdy.jzyy.entity.boss.entity.DogJiaoJiaoJiao;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.effect.register.EffectRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DogJiaoJiaoJiaoAttackGoal extends MeleeAttackGoal {
    private final DogJiaoJiaoJiao dogEntity;
    private int attackDelay = 0;
    private boolean isPreparingAttack = false;
    private boolean isAttacking = false;
    private int preparationTicks = 0;
    private int attackDuration = 0;
    private static final int PREPARATION_TIME = 12; // 0.6秒准备 (12刻)
    private static final int ATTACK_DURATION = 8; // 0.4秒攻击持续时间 (8刻)
    private static final double ATTACK_RANGE_LENGTH = 16.0; // 攻击范围长度
    private static final double ATTACK_RANGE_WIDTH = 6.0; // 攻击范围宽度

    public DogJiaoJiaoJiaoAttackGoal(DogJiaoJiaoJiao pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.dogEntity = pMob;
    }

    @Override
    public boolean canUse() {
        // 只在非吼叫动画播放期间才允许攻击
        if (dogEntity.isScreaming() && dogEntity.screamTicks > 0) {
            return false;
        }

        LivingEntity target = this.dogEntity.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        // 使用自定义的攻击范围检查
        double distanceToTarget = this.dogEntity.distanceToSqr(target);
        return distanceToTarget <= this.getAttackReachSqr(target) &&
                this.dogEntity.getSensing().hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.dogEntity.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        // 如果开始吼叫，就中断攻击
        if (dogEntity.isScreaming() && dogEntity.screamTicks > 0) {
            return false;
        }

        // 允许在攻击准备和攻击期间继续使用
        if (isPreparingAttack || isAttacking) {
            return true;
        }

        return super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        this.attackDelay = 0;
        this.isPreparingAttack = false;
        this.isAttacking = false;
        this.preparationTicks = 0;
        this.attackDuration = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.isPreparingAttack = false;
        this.isAttacking = false;
        this.preparationTicks = 0;
        this.attackDuration = 0;
        // 重置攻击状态
        this.dogEntity.setAttacking(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.dogEntity.getTarget();

        if (target == null || !target.isAlive()) {
            this.stop();
            return;
        }

        // 检查距离是否足够近
        double distanceToTarget = this.dogEntity.distanceToSqr(target);
        double attackRange = this.getAttackReachSqr(target);

        if (isAttacking) {
            // 正在攻击中
            attackDuration++;

            // 攻击持续时间结束
            if (attackDuration >= ATTACK_DURATION) {
                isAttacking = false;
                attackDuration = 0;
                attackDelay = 20; // 攻击后冷却1秒
                this.dogEntity.setAttacking(false);
            }
        } else if (isPreparingAttack) {
            // 正在准备攻击
            preparationTicks++;

            // 面向目标
            this.dogEntity.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // 准备时间结束，执行攻击
            if (preparationTicks >= PREPARATION_TIME) {
                executeAreaAttack();
                isPreparingAttack = false;
                isAttacking = true;
                preparationTicks = 0;
            }
        } else if (attackDelay > 0) {
            // 攻击冷却中
            attackDelay--;

            // 冷却期间可以移动
            super.tick();
        } else if (distanceToTarget <= attackRange && this.dogEntity.getSensing().hasLineOfSight(target)) {
            // 开始准备攻击
            startAttackPreparation();
        } else {
            // 普通移动逻辑 - 接近目标
            super.tick();
        }
    }

    private void startAttackPreparation() {
        this.isPreparingAttack = true;
        this.preparationTicks = 0;
        this.dogEntity.setAttacking(true);

        // 停止移动，准备攻击
        this.dogEntity.getNavigation().stop();
    }

    private void executeAreaAttack() {
        if (this.dogEntity.level().isClientSide) {
            return;
        }

        LivingEntity target = this.dogEntity.getTarget();
        if (target == null) {
            return;
        }

        // 计算攻击方向（面向目标）
        Vec3 lookAngle = this.dogEntity.getLookAngle();
        Vec3 attackStart = this.dogEntity.position().add(0, this.dogEntity.getEyeHeight(), 0);

        // 定义攻击范围（3宽×6长的矩形区域）
        AABB attackArea = calculateAttackArea(attackStart, lookAngle);

        // 获取范围内的所有实体
        List<LivingEntity> entitiesInRange = this.dogEntity.level().getEntitiesOfClass(
                LivingEntity.class,
                attackArea,
                entity -> entity != this.dogEntity && entity.isAlive()
        );

        // 对范围内的实体造成伤害
        for (LivingEntity entity : entitiesInRange) {
            if (entity.getHealth() <= 5f) entity.setHealth(0);
            // 造成伤害（基于实体基础攻击力）
            float damage = (float) this.dogEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            entity.hurt(this.dogEntity.damageSources().mobAttack(this.dogEntity), damage * 10);
            entity.addEffect(new MobEffectInstance(EffectRegister.PHYSICALINJURY.get(),200,0));

            // 添加击退效果
            Vec3 knockbackDir = entity.position().subtract(this.dogEntity.position()).normalize();
            entity.setDeltaMovement(
                    knockbackDir.x * 0.8,
                    Math.min(0.4, entity.getDeltaMovement().y + 0.2),
                    knockbackDir.z * 0.8
            );
        }

        // 播放攻击音效
        this.dogEntity.playSound(JzyySoundsRegister.SCREAM3.get(), 2.0F, 1F);
    }

    private AABB calculateAttackArea(Vec3 startPos, Vec3 direction) {
        // 将方向向量标准化
        Vec3 normalizedDir = direction.normalize();

        // 计算攻击区域的终点
        Vec3 endPos = startPos.add(normalizedDir.scale(ATTACK_RANGE_LENGTH));

        // 计算垂直于方向的向量（用于确定宽度）
        Vec3 perpendicular;
        if (Math.abs(normalizedDir.y) > 0.9) {
            // 如果方向主要是垂直的，使用X轴作为垂直方向
            perpendicular = new Vec3(1, 0, 0);
        } else {
            // 计算水平垂直方向
            perpendicular = new Vec3(-normalizedDir.z, 0, normalizedDir.x).normalize();
        }

        // 计算攻击区域的四个角点
        Vec3 halfWidth = perpendicular.scale(ATTACK_RANGE_WIDTH / 2);
        Vec3 corner1 = startPos.add(halfWidth);
        Vec3 corner2 = startPos.subtract(halfWidth);
        Vec3 corner3 = endPos.add(halfWidth);
        Vec3 corner4 = endPos.subtract(halfWidth);

        // 创建包含所有角点的AABB
        double minX = Math.min(Math.min(corner1.x, corner2.x), Math.min(corner3.x, corner4.x));
        double minY = Math.min(Math.min(corner1.y, corner2.y), Math.min(corner3.y, corner4.y)) - 1.0;
        double minZ = Math.min(Math.min(corner1.z, corner2.z), Math.min(corner3.z, corner4.z));
        double maxX = Math.max(Math.max(corner1.x, corner2.x), Math.max(corner3.x, corner4.x));
        double maxY = Math.max(Math.max(corner1.y, corner2.y), Math.max(corner3.y, corner4.y)) + 1.0;
        double maxZ = Math.max(Math.max(corner1.z, corner2.z), Math.max(corner3.z, corner4.z));

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        // 增加攻击触发范围
        double baseReach = super.getAttackReachSqr(pAttackTarget);
        return baseReach + 4.0; // 增加4格范围
    }

    // 检查是否正在攻击（用于动画控制器）
    public boolean isAttacking() {
        return isPreparingAttack || isAttacking;
    }
}
