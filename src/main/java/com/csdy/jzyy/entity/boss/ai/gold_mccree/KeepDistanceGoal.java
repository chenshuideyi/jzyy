package com.csdy.jzyy.entity.boss.ai.gold_mccree;

import com.csdy.jzyy.entity.boss.entity.GoldMcCree;
import icyllis.modernui.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class KeepDistanceGoal extends Goal {
    protected final GoldMcCree mob;
    protected final double speedModifier;
    protected final float minDistance;
    protected final float maxDistance;
    protected LivingEntity target;
    protected int interval;
    protected boolean forceTrigger;
    private int cooldown = 0; // 冷却计时器

    public KeepDistanceGoal(GoldMcCree mob, double speedModifier, float minDistance, float maxDistance) {
        this(mob, speedModifier, minDistance, maxDistance, 20);
    }

    public KeepDistanceGoal(GoldMcCree mob, double speedModifier, float minDistance, float maxDistance, int interval) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.interval = interval;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // 冷却期间不执行
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        LivingEntity target = this.mob.getTarget();

        // 没有目标或者目标是载具时不执行
        if (target == null || target.isRemoved() || this.mob.isVehicle()) {
            return false;
        }

        // 控制触发频率
        if (!this.forceTrigger) {
            if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                return false;
            }
        }

        this.target = target;
        float currentDistance = this.mob.distanceTo(this.target);

        // 只有距离不合适时才移动
        return currentDistance < minDistance || currentDistance > maxDistance;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null || this.target.isRemoved() || this.mob.isVehicle()) {
            return false;
        }

        float currentDistance = this.mob.distanceTo(this.target);

        // 如果导航完成但距离仍然不合适，继续移动
        if (this.mob.getNavigation().isDone()) {
            return currentDistance < minDistance || currentDistance > maxDistance;
        }

        return true;
    }

    @Override
    public void start() {
        // 寻找合适的位置
        Vec3 movePos = findSuitablePosition();
        if (movePos != null) {
            boolean success = this.mob.getNavigation().moveTo(movePos.x, movePos.y, movePos.z, this.speedModifier);
            if (!success) {
                // 如果路径寻找失败，设置短暂冷却
                cooldown = 0;
            }
        } else {
            // 如果找不到位置，设置短暂冷却
            cooldown = 0;
        }
        this.forceTrigger = false;
    }

    @Override
    public void tick() {
        if (this.mob.getNavigation().isDone() && this.target != null) {
            float currentDistance = this.mob.distanceTo(this.target);
            if (currentDistance < minDistance || currentDistance > maxDistance) {
                Vec3 newPos = findSuitablePosition();
                if (newPos != null) {
                    // 移动时保持当前朝向，不自动转向
                    this.mob.getNavigation().moveTo(newPos.x, newPos.y, newPos.z, this.speedModifier);
                }
            }
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
        // 停止时设置短暂冷却，避免立即重新开始
        cooldown = 0;
    }

    @Nullable
    protected Vec3 findSuitablePosition() {
        if (this.target == null) {
            return null;
        }

        float currentDistance = this.mob.distanceTo(this.target);
        Vec3 targetPos = this.target.position();
        Vec3 mobPos = this.mob.position();

        // 计算理想距离向量
        Vec3 idealDirection;

        if (currentDistance < minDistance) {
            // 太近了，需要远离目标
            idealDirection = mobPos.subtract(targetPos).normalize();
        } else if (currentDistance > maxDistance) {
            // 太远了，需要靠近目标
            idealDirection = targetPos.subtract(mobPos).normalize();
        } else {
            // 距离合适，轻微调整位置（保持面向目标）
            double angle = this.mob.getRandom().nextDouble() * Math.PI - Math.PI / 2; // -90° 到 +90°
            idealDirection = targetPos.subtract(mobPos).normalize().yRot((float) angle);
        }

        // 计算目标位置
        double idealDistance = (minDistance + maxDistance) / 2.0;
        Vec3 idealPos = targetPos.add(idealDirection.scale(idealDistance));

        // 在地面高度上寻找可行走的位置
        return DefaultRandomPos.getPosTowards(this.mob, 12, 7, idealPos, (float) (Math.PI / 2));
    }

    public void trigger() {
        this.forceTrigger = true;
        this.cooldown = 0; // 强制触发时重置冷却
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
