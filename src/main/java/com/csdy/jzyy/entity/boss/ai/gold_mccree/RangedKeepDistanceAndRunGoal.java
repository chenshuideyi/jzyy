package com.csdy.jzyy.entity.boss.ai.gold_mccree;

import com.Polarice3.Goety.utils.BlockFinder;
import com.csdy.jzyy.entity.boss.entity.GoldMcCree;


import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionSeverance;

public class RangedKeepDistanceAndRunGoal extends KeepDistanceGoal {

    private int attackCooldown = -1;
    private final int attackInterval;
    private final float attackRadius;
    private int attackedTimes = 0;
    private Vec3 lastMovePos;
    private int blinkPrepare = -1;

    // 添加移动状态控制
    private boolean shouldMove = false;
    private int moveCooldown = 0;

    public RangedKeepDistanceAndRunGoal(GoldMcCree mob, double speedModifier,
                                        float minDistance, float maxDistance,
                                        int attackInterval, float attackRadius) {
        super(mob, speedModifier, minDistance, maxDistance);
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
    }

    @Override
    public void tick() {

        if (this.mob.getPhase() == 1) return;

        if (this.target == null) {
            this.target = this.mob.getTarget();
            if (this.target == null) return;
        }

        // 完全停止导航系统，我们自己控制移动
        this.mob.getNavigation().stop();

        // 强制面向目标
        forceLookAtTarget();

        // 自定义移动逻辑
        updateCustomMovement();

        // 尝试瞬移
        updateBlink();

        // 攻击逻辑
        updateAttack();

        System.out.println("执行边跑边射 - 移动: " + shouldMove + " | 攻击冷却: " + attackCooldown);
    }

    private void updateCustomMovement() {
        float currentDistance = this.mob.distanceTo(this.target);

        // 移动冷却
        if (moveCooldown > 0) {
            moveCooldown--;
            if (moveCooldown <= 0) {
                shouldMove = false;
            }
        }

        // 检查距离是否需要开始新的移动周期
        if ((currentDistance < minDistance || currentDistance > maxDistance) && !shouldMove && moveCooldown <= 0) {
            shouldMove = true;
            moveCooldown = 70; // 移动2.5秒 + 冷却2秒
            this.mob.setMovingWithDuration(true);
        }

        // 执行移动
        if (shouldMove) {
            Vec3 movePos = findSuitablePosition();
            if (movePos != null) {
                // 手动移动，不通过导航系统
                Vec3 moveDir = movePos.subtract(this.mob.position()).normalize();

                // 设置移动速度
                double moveSpeed = this.speedModifier * 0.3;
                this.mob.setDeltaMovement(
                        moveDir.x * moveSpeed,
                        this.mob.getDeltaMovement().y,
                        moveDir.z * moveSpeed
                );

                lastMovePos = movePos;
            }
        } else {
            // 停止移动
            this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
        }

        // 应用移动
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
    }

    private void updateBlink() {
        if (attackedTimes >= 12) {
            blinkPrepare = 4;
            attackCooldown = 8; // 这个8是考虑到瞬移后摇的值，前摇4后摇4很合理
            attackedTimes = 0;
            this.mob.setBlinking(true);
        }

        if (blinkPrepare > 0) {
            blinkPrepare--;
        }

        if (blinkPrepare == 0) {
            blinkPrepare = -1;
            teleport();
            this.mob.setBlinking(false);
        }
    }

    private void updateAttack() {
        float distance = this.mob.distanceTo(this.target);
        boolean inRange = distance <= this.attackRadius;
        boolean canSee = this.mob.getSensing().hasLineOfSight(this.target);

        this.attackCooldown--;
        if (canSee && inRange && this.attackCooldown <= 0) {
            performRangedAttack();
            shoot(target);
            this.attackedTimes++;
            this.attackCooldown = this.attackInterval;
        }
    }

    private void forceLookAtTarget() {
        if (this.target == null) return;

        // 计算到目标的方向
        double dx = this.target.getX() - this.mob.getX();
        double dz = this.target.getZ() - this.mob.getZ();

        // 计算目标朝向（角度）
        float targetYRot = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;

        // 直接设置所有朝向相关变量
        this.mob.setYRot(targetYRot);
        this.mob.setYBodyRot(targetYRot);
        this.mob.setYHeadRot(targetYRot);
        this.mob.yBodyRot = targetYRot;
        this.mob.yHeadRot = targetYRot;
        this.mob.yRotO = targetYRot;

        // 同时设置 LookControl
        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
    }

    private void performRangedAttack() {
        // 使用持续时间方法设置攻击状态
        this.mob.setAttackingWithDuration(true);
        System.out.println("开始射击 - 持续2.5秒");
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAttacking(false);
        this.mob.setMovingWithDuration(false);
        forceLookAtTarget();
        // 停止移动
        this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
        System.out.println("停止边跑边射");
        teleport();
        this.attackCooldown = -1;
        this.lastMovePos = null;
        this.shouldMove = false;
        this.moveCooldown = 0;
    }

    @Override
    public void start() {
        this.mob.setAttacking(true);
        this.target = this.mob.getTarget();
        forceLookAtTarget();
        this.mob.getNavigation().stop();
        System.out.println("开始边跑边射模式");
    }

    private void shoot(LivingEntity target) {
        this.mob.level.getServer().execute(() -> {
            if (this.mob.isAlive() && target.isAlive()) {
                this.mob.playSound(JzyySoundsRegister.SHOOT.get(), 2.0F, 1F);
                reflectionSeverance(target, target.getHealth() - 158); // 重复播这一大段意义不大，有需要的话你可以只重复这行
            }
        });
    }

    private void teleport() {
        if (!this.mob.level.isClientSide() && this.mob.isAlive()) {
            double prevX = this.mob.getX();
            double prevY = this.mob.getY();
            double prevZ = this.mob.getZ();
            for(int i = 0; i < 128; ++i) {
                boolean flag = true;
                double d3 = prevX + (this.mob.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = prevY;
                if (this.mob.getTarget() != null){
                    d4 = this.mob.getTarget().getY();
                }
                double d5 = prevZ + (this.mob.getRandom().nextDouble() - 0.5D) * 32.0D;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                if (this.mob.getTarget() != null && i < 64) {
                    flag = canSeeBlock(mob.getTarget(), blockPos);
                }
                if (flag) {
                    if (mob.randomTeleport(d3, d4, d5, false)) {
                        this.mob.setBlinking(true);
                        this.attackCooldown = 40; // 奇技淫巧之：在这里设置攻击冷却然后冷却好就清掉状态
                        break;
                    }
                }
            }
        }
    }

    public static boolean canSeeBlock(Entity looker, BlockPos location) {
        Vec3 vec3 = new Vec3(looker.getX(), looker.getEyeY(), looker.getZ());
        Vec3 vec31 = Vec3.atBottomCenterOf(location);
        if (vec31.distanceTo(vec3) > (double)128.0F) {
            return false;
        } else {
            return looker.level.clip(new ClipContext(vec3, vec31, net.minecraft.world.level.ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, looker)).getType() == HitResult.Type.MISS;
        }
    }

//    private void performTeleport() {
//        if (this.target == null || !this.target.isAlive()) {
//            Minecraft.LOGGER.atInfo().log("<GMCR> 闪了但是没闪：shan le dan shi mei shan");
//            return;
//        }
//
//        Vec3 targetPos = this.target.position();
//
//        // 尝试多个随机方向找到合适的瞬移位置
//        for (int attempt = 0; attempt < 10; attempt++) {
//            // 随机角度
//            double angle = this.mob.getRandom().nextDouble() * Math.PI * 2;
//            Vec3 teleportDirection = new Vec3(Math.cos(angle), 0, Math.sin(angle));
//
//            // 计算瞬移位置（7格距离）
//            Vec3 teleportPos = targetPos.add(teleportDirection.scale(7.0));
//
//            // 寻找地面位置
//            Vec3 groundPos = findGroundPosition(teleportPos);
//
//            if (groundPos != null && isPositionSafe(groundPos)) {
//                // 执行瞬移
//                this.mob.teleportTo(groundPos.x, groundPos.y, groundPos.z);
//                this.mob.setBlinking(true);
//                this.attackCooldown = 40; // 奇技淫巧之：在这里设置攻击冷却然后冷却好就清掉状态
//                Minecraft.LOGGER.atInfo().log("<GMCR>成功瞬移到: %s, Yattaze~".formatted(groundPos));
//                System.out.println();
//                return;
//            }
//            Minecraft.LOGGER.atInfo().log("<GMCR>这个位置不能用: %s, Ezattay:(".formatted(groundPos));
//        }
//
//        Minecraft.LOGGER.atInfo().log("<GMCR>找不到合适的瞬移位置: zhao bu dao he shi wei zhi");
//    }
//
//    private boolean isPositionSafe(Vec3 pos) {
//        BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
//        Level level = this.mob.level;
//
//        // 检查位置是否安全（不在方块内，有足够的空间）
//        return !level.getBlockState(blockPos).isSolid() &&
//                !level.getBlockState(blockPos.above()).isSolid() &&
//                level.getBlockState(blockPos.below()).isSolid();
//    }
//
//    private Vec3 findGroundPosition(Vec3 pos) {
//        // 寻找可行的地面位置
//        BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
//        Level level = this.mob.level;
//
//        // 向下寻找地面
//        while (blockPos.getY() > level.getMinBuildHeight() &&
//                !level.getBlockState(blockPos).isSolid()) {
//            blockPos = blockPos.below();
//        }
//
//        // 向上寻找可行走的位置
//        while (blockPos.getY() < level.getMaxBuildHeight() &&
//                (level.getBlockState(blockPos).isSolid() ||
//                        !level.getBlockState(blockPos.above()).isAir())) {
//            blockPos = blockPos.above();
//        }
//
////        // 确保牛魔啊确保，上面都提高到不是Solid才停了这儿还检查是Solid，那不始终为false然后返回null了吗
////        // 确保位置可行走
////        if (level.getBlockState(blockPos).isSolid() &&
////                level.getBlockState(blockPos.above()).isAir() &&
////                level.getBlockState(blockPos.above(2)).isAir()) {
////            return new Vec3(blockPos.getX() + 0.5, blockPos.above().getY(), blockPos.getZ() + 0.5);
////        }
//
//        return null;
//    }
//
//        return null;

    private boolean isSuffocating(Level level, BlockPos pos) {
        return level.getBlockState(pos).isSuffocating(level, pos);
    }

    private boolean isStandable(Level level, BlockPos pos) {
        return level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP);
    }

}