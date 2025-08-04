package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.boss.ai.mizi.MiziDrinkGoal;
import com.csdy.jzyy.entity.boss.ai.mizi.MiziMeleeGoal;
import com.csdy.jzyy.entity.boss.ai.PersistentHurtByTargetGoal;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class MiziAo extends BossEntity implements GeoEntity {

    private static Diadema musicGame;

    private int drinkingTicks;
    public static final int RATING = 15203;
    public static final int PERFECT = 101;
    private boolean isEnraging = false;
    private boolean lastChujingState;

    // --- 状态同步: 这是修复所有问题的核心 ---
    // 为“出警”状态定义数据访问器 (用于持久化和同步)
    private static final EntityDataAccessor<Boolean> DATA_IS_CHUJING =
            SynchedEntityData.defineId(MiziAo.class, EntityDataSerializers.BOOLEAN);
    // 为“攻击中”状态定义数据访问器 (仅用于动画同步)
    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING =
            SynchedEntityData.defineId(MiziAo.class, EntityDataSerializers.BOOLEAN);
    // 为“喝魔爪”状态定义数据访问器 (仅用于动画同步)
    private static final EntityDataAccessor<Boolean> DATA_IS_DRINKING =
            SynchedEntityData.defineId(MiziAo.class, EntityDataSerializers.BOOLEAN);


    // --- 动画定义 ---
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.attack");
    private static final RawAnimation STAND_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.stand");
    private static final RawAnimation DRINK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.drink");

    private static final RawAnimation ANGRY_STAND_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.angry_stand");
    private static final RawAnimation ANGRY_WALK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.angry_walk");
    private static final RawAnimation ANGRY_ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.angry_attack");
    private static final RawAnimation ANGRY_DRINK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.angry_drink");

    private static final double BREAK_BOX_WIDTH = 4.0;  // 区域宽度
    private static final double BREAK_BOX_HEIGHT = 4.0; // 区域高度
    private static final double BREAK_BOX_DEPTH = 3.0;  // 区域深度（向前延伸多远）

    /**
     * 破坏实体正前方的方块。
     * 这个方法会被AI Goal在攻击动画期间调用。
     */
    public void breakBlocksInFront() {
        if (this.level().isClientSide) {
            return; // 破坏方块的逻辑只在服务器上执行
        }

        // 1. 计算破坏区域的包围盒 (AABB)
        // 从实体眼睛位置开始，向前延伸一定距离作为中心点
        Vec3 boxCenter = this.getEyePosition().add(this.getLookAngle().scale(BREAK_BOX_DEPTH / 2.0));
        // 以中心点创建包围盒
        AABB breakArea = new AABB(boxCenter, boxCenter).inflate(BREAK_BOX_WIDTH / 2.0, BREAK_BOX_HEIGHT / 2.0, BREAK_BOX_DEPTH / 2.0);

        // 2. 遍历包围盒内的所有方块
        for (BlockPos pos : BlockPos.betweenClosed(
                BlockPos.containing(breakArea.minX, breakArea.minY, breakArea.minZ),
                BlockPos.containing(breakArea.maxX, breakArea.maxY, breakArea.maxZ)
        )) {
            if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) >= 0) {
                this.level().getBlockState(pos).isAir();
            }
        }
    }



    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent;
    public MiziAo(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lastChujingState = false;
        this.bossEvent = new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS); // 血条样式
        if (level.isClientSide) return;
        musicGame = JzyyDiademaRegister.MUSIC_GAME.get().CreateInstance(new FollowDiademaMovement(this));
    }



    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        // 注册同步数据并设置默认值
        this.entityData.define(DATA_IS_CHUJING, false);
        this.entityData.define(DATA_IS_ATTACKING, false);
        this.entityData.define(DATA_IS_DRINKING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void tick() {
        super.tick();

        updateDynamicName();
        manageSpeedModifier();

        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.bossEvent.setName(this.getDisplayName());

            if (this.drinkingTicks > 0) {
                this.drinkingTicks--;

                // 当喝饮料计时刚刚结束时...
                if (this.drinkingTicks <= 0) {
                    // 如果这次喝饮料是因为被激怒了...
                    if (this.isEnraging) {
                        // 那么现在，在动画播放完毕后，才真正进入“出警”状态
                        this.setChujing(true);
                        // 并重置这个一次性的标志
                        this.isEnraging = false;
                    }

                    this.spawnLingeringCloudAttack();
                    // 无论如何，都结束喝饮料的动画状态
                    this.setDrinking(false);
                }
            }

            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && !this.isAttacking() && this.drinkingTicks <= 0) {
                double distanceSqr = this.distanceToSqr(target);
                double attackRangeSqr = this.getMeleeAttackRangeSqr();
                if (distanceSqr > attackRangeSqr) {
                    this.startDrinkingSequence();
                }
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        // 确保当Boss死亡或被移除时，血条也从所有玩家屏幕上消失
        this.bossEvent.removeAllPlayers();
    }



    // --- 速度修改器 ---
    private static final UUID CHUJING_SPEED_BOOST_UUID = UUID.fromString("78781313-1378-4b0e-1111-23380e8b0f9b");
    private static final AttributeModifier CHUJING_SPEED_BOOST = new AttributeModifier(
            CHUJING_SPEED_BOOST_UUID, "mizi_speed", 2.0D, AttributeModifier.Operation.MULTIPLY_BASE);

    private void manageSpeedModifier() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) return;
        if (this.isChujing()) {
            if (!attributeInstance.hasModifier(CHUJING_SPEED_BOOST)) {
                attributeInstance.addTransientModifier(CHUJING_SPEED_BOOST);
            }
        } else {
            if (attributeInstance.hasModifier(CHUJING_SPEED_BOOST)) {
                attributeInstance.removeModifier(CHUJING_SPEED_BOOST);
            }
        }
    }


    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        float actualDamage = Math.min(amount, PERFECT);
        if (source.getDirectEntity() instanceof Player player) {

            boolean isCritical = player.fallDistance > 0.0F && !player.onGround() && !player.isSwimming() &&
                    !player.isPassenger() && !player.hasEffect(MobEffects.BLINDNESS) && // 加上原版暴击需要的“非失明”条件
                    player.getAttackStrengthScale(0.5F) > 0.9F;

            if (isCritical && !this.isChujing() && !this.isEnraging) {
                // 设置“正在激怒中”的标志位，防止重复触发
                this.isEnraging = true;

                this.startDrinkingSequence();
            }
        }

        return super.hurt(source, actualDamage);
    }

    // --- AI ---
    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new MiziDrinkGoal(this));
        this.goalSelector.addGoal(2, new MiziMeleeGoal(this, 1.0D, true));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new PersistentHurtByTargetGoal(this));
    }

    // --- 动画 ---
    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<MiziAo> state) {
        boolean isChujing = this.isChujing();

        if (this.isDrinking()) {
            return state.setAndContinue(isChujing ? ANGRY_DRINK_ANIM : DRINK_ANIM);
        }

        // 优先级 1: 攻击动画 (保持不变)
        if (this.isAttacking()) {
            return state.setAndContinue(isChujing ? ANGRY_ATTACK_ANIM : ATTACK_ANIM);
        }

        // 优先级 2: 行走动画 (保持不变)
        if (state.isMoving()) {
            return state.setAndContinue(isChujing ? ANGRY_WALK_ANIM : WALK_ANIM);
        }

        // --- 优先级 3: 站立/待机动画 (这里是修改的核心) ---
        // 这个代码块只会在Boss既没有攻击也没有移动时执行

        // 如果处于“出警”状态，则总是播放愤怒站立动画
        if (isChujing) {
            return state.setAndContinue(ANGRY_STAND_ANIM);
        } else {
            // 如果不处于“出警”状态，则检查是否有仇恨目标
            LivingEntity target = this.getTarget(); // 获取当前仇恨目标

            if (target != null && target.isAlive()) {
                // 如果有存活的仇恨目标，播放“备战”站姿
                return state.setAndContinue(STAND_ANIM);
            } else {
                // 如果没有任何目标，播放和平的“闲置”姿态
                return state.setAndContinue(IDLE_ANIM);
            }
        }
    }

    // --- Getters and Setters for Synched Data ---
    public boolean isChujing() {
        return this.entityData.get(DATA_IS_CHUJING);
    }

    public void setChujing(boolean chujing) {
        this.entityData.set(DATA_IS_CHUJING, chujing);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACKING, attacking);
    }

    public boolean isDrinking() {
        return this.entityData.get(DATA_IS_DRINKING);
    }

    public void setDrinking(boolean drinking) {
        this.entityData.set(DATA_IS_DRINKING, drinking);
    }

    /**
     * 启动喝饮料的动画和计时器
     */
    private void startDrinkingSequence() {
        this.drinkingTicks = 30; // 设置动画持续时间为40 ticks (2秒)
        this.setDrinking(true);  // 设置同步数据，通知客户端播放动画
    }

    /**
     * 获取实体当前的近战攻击距离的平方。
     * 这对于在tick中判断玩家是否超出范围至关重要。
     * @return 攻击距离的平方
     */
    private double getMeleeAttackRangeSqr() {
        if (this.isChujing()) {
            return 6.0D * 6.0D; // 出警状态下的攻击范围
        }
        // 非出警状态下的攻击范围（需要与MeleeGoal中的默认值保持一致）
        // 通常MeleeAttackGoal的默认值是基于实体碰撞箱的，这里我们用一个近似值
        return (1.5D + this.getBbWidth()) * (1.5D + this.getBbWidth());
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, RATING)
                .add(Attributes.ARMOR, RATING)
                .add(Attributes.ATTACK_DAMAGE, PERFECT)
                .add(Attributes.ATTACK_SPEED, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.geoCache; }

    @Override
    public SoundEvent getBossMusic() { return JzyySoundsRegister.UMIYURI_KAITEITAN.get(); }



    @Override
    public boolean isNoAi() { return false; }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    private void spawnLingeringCloudAttack() {
        if (this.level().isClientSide) {
            return; // 此逻辑只在服务器端执行
        }

        // --- 攻击参数，您可以在这里微调 ---
        final int rows = 3;             // 总共生成3行
        final double rowSpacing = 5;  // 每行之间的距离
        final int cloudsPerRow = 8;     // 每行生成5个云
        final double cloudSpacing = 5;// 每行中，云与云之间的距离
        final float cloudRadius = 2.5F; // 每个云的半径
        final int cloudDuration = 20 * 15; // 每个云的持续时间 (15秒)
        // ------------------------------------

        // 获取Boss的正前方和正右方向量
        Vec3 forwardVec = this.getLookAngle();
        Vec3 rightVec = forwardVec.yRot((float) (Math.PI / 2.0)); // 旋转90度得到右方向量

        // 遍历生成每一行
        // (i - 1) 会让循环变为 -1, 0, 1，分别代表左、中、右三行
        for (int i = 0; i < rows; i++) {
            // 计算当前行的偏移向量
            double offsetMagnitude = (i - 1) * rowSpacing;
            Vec3 rowOffsetVec = rightVec.scale(offsetMagnitude);

            // 在当前行中生成每一个云
            for (int j = 0; j < cloudsPerRow; j++) {
                // 计算当前云的位置
                Vec3 cloudPosVec = this.position() // 从Boss脚下开始
                        .add(forwardVec.scale(j * cloudSpacing + 2.0)) // 向前延伸
                        .add(rowOffsetVec); // 应用左/中/右的行偏移

                // 创建并配置药水云实体
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), cloudPosVec.x, cloudPosVec.y, cloudPosVec.z);
                cloud.setOwner(this);
                cloud.setRadius(cloudRadius);
                cloud.setDuration(cloudDuration);
                cloud.setWaitTime(10); // 出现后等待10 ticks才开始生效和缩小
                cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration()); // 设置使其在持续时间内半径缩为0

                // --- 添加所有您指定的强力效果 ---
                // 参数：(效果, 持续时间(ticks), 等级(amplifier, 0=1级))
                // 等级255对应amplifier为254，但对于如此高的值，直接用255也无妨
                cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 15, 255));
                cloud.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 15, 255));
                cloud.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 15, 255));
                cloud.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 15, 255));
                cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 15, 255));
                cloud.addEffect(new MobEffectInstance(MobEffects.JUMP, 20 * 15, 128));


                // 将配置好的云添加到世界中
                this.level().addFreshEntity(cloud);
            }
        }
    }

    private void updateDynamicName() {
        boolean currentState = this.isChujing();
        // 只有当状态与上一tick不同时，才进行更新
        if (currentState != this.lastChujingState) {
            Component newName;
            if (currentState) {
                newName = Component.translatable("entity.jzyy.mizi_ao.chujing");
            } else {
                newName = Component.translatable("entity.jzyy.mizi_ao");
            }

            // 主动设置自定义名称，这将自动同步到客户端
            this.setCustomName(newName);
            // 确保名字总是可见的（可选，但推荐）
            this.setCustomNameVisible(true);

            // 更新记录的状态
            this.lastChujingState = currentState;
        }
    }

//    @Override
//    public @NotNull Component getDisplayName() {
//        if(isChujing()){
//            return Component.translatable("entity.jzyy.mizi_ao.chujing");
//        }
//        else return Component.translatable("entity.jzyy.mizi_ao");
//    }
}
