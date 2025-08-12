package com.csdy.jzyy.entity.boss.ai.TitanWarden;

import com.csdy.jzyy.entity.boss.entity.TitanWarden;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TitanWardenAttackGoal extends MeleeAttackGoal {
    private final TitanWarden titanWarden;

    private int attackSequenceTicks;

    public TitanWardenAttackGoal(TitanWarden mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.titanWarden = mob;
    }


    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        double attackReachSqr = this.getAttackReachSqr(target);

        if (squaredDistance <= attackReachSqr && this.getTicksUntilNextAttack() <= 0
                && this.attackSequenceTicks <= 0&&!titanWarden.isRemote()&&!titanWarden.isLocking()) {

            this.resetAttackCooldown();

            this.titanWarden.setAttacking(true);

            this.attackSequenceTicks = 7;
        }
    }

    @Override
    public void tick() {
        super.tick(); // 必须调用，以保证Boss在蓄力时也能追击目标

        // 如果我们正处在一个攻击序列中...
        if (this.attackSequenceTicks > 0) {
            // 递减计时器
            this.attackSequenceTicks--;

            // 在动画即将结束的瞬间 (例如，计时器为1时) 尝试造成伤害
            if (this.attackSequenceTicks == 1) {
                breakBlocksInFront();
                // (您已有的伤害逻辑保持不变)
                LivingEntity target = this.mob.getTarget();
                if (target != null) {
                    double distanceSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                    double reachSqr = this.getAttackReachSqr(target);
                    if (distanceSqr <= reachSqr) {
                        if (this.titanWarden.isLock()) {
                            float a = (float) this.titanWarden.getAttributeValue(Attributes.ATTACK_DAMAGE);
                            target.hurt(this.titanWarden.damageSources().mobAttack(this.titanWarden), a);
                        } else {
                            this.titanWarden.doHurtTarget(target);
                        }
                    }
                }
            }

            // 当计时器结束时，停止播放动画
            if (this.attackSequenceTicks <= 0) {
                this.titanWarden.setAttacking(false);
            }
        }
    }

    public void breakBlocksInFront() {
        Level level = this.titanWarden.level();
        if (this.titanWarden.level().isClientSide) {
            return;
        }
        AABB box = this.titanWarden.getBoundingBox();   // 碰撞箱
        int yStart = (int) Math.floor(box.minY - 0.001); // 脚下第一格
        int yEnd   = level.getMinBuildHeight();
        // 遍历水平区域
        for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
            for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {
                for (int y = yStart; y >= yEnd; y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    // 这里可按需过滤：硬度、不可破坏方块、需要工具等
                    if (state.isAir() || state.getDestroySpeed(level, pos) < 0) continue;
                    // 直接破坏（无掉落）
                    level.destroyBlock(pos, false);

                }
            }
        }
    }
    @Override
    public void stop() {
        super.stop();
        this.attackSequenceTicks = 0;
        this.titanWarden.setAttacking(false);
    }

    // getAttackReachSqr 方法保持不变
    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return super.getAttackReachSqr(target);
    }
}
