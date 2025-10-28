package com.csdy.jzyy.entity.boss.ai.TitanWarden;

import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.csdy.jzyy.entity.boss.entity.TitanWarden;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

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

            this.attackSequenceTicks = 116;
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
            if (this.attackSequenceTicks == 44) {
//                breakBlocksInFront();

                LivingEntity target = this.mob.getTarget();
                if (target != null) {
                    double reachSqr = this.getAttackReachSqr(target);
                    Vec3 vec3 = this.titanWarden.position();
                    AABB aabb = new AABB(
                            vec3.x - reachSqr, vec3.y - this.titanWarden.getBbWidth()*0.2f, vec3.z - reachSqr,
                            vec3.x + reachSqr, vec3.y + this.titanWarden.getBbWidth()*0.2f, vec3.z + reachSqr
                    );
                    float a = (float) this.titanWarden.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    List<LivingEntity> list=this.titanWarden.level().getEntitiesOfClass(LivingEntity.class,aabb);
                    for (LivingEntity living:list) {
                        if (living!=this.titanWarden) {
                            living.invulnerableTime = 0;
                            living.hurt(LegacyDamageSource.mobAttack(this.titanWarden), a);
                            Vec3 vec = living.position().subtract(this.titanWarden.position()).normalize().scale(10);
                            living.push(vec.x, this.titanWarden.getBbWidth()*0.8f, vec.z);
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
        Vec3 vec3 = this.titanWarden.position();
        int yEnd = (int) Math.floor(vec3.y - 1.001);
        double a = this.getAttackReachSqr(this.titanWarden)*0.5f;
        // 遍历水平区域
        for (int x = (int) Math.floor(vec3.x-a); x <= (int) Math.floor(vec3.x+a); x++) {
            for (int z = (int) Math.floor(vec3.z-a); z <= (int) Math.floor(vec3.z+a); z++) {
                for (int y = (int) Math.floor(vec3.y+this.titanWarden.getBbHeight()*0.2f); y >= yEnd; y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    // 这里可按需过滤：硬度、不可破坏方块、需要工具等
                    if (state.isAir() || state.getDestroySpeed(level, pos) < 0) continue;
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

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
