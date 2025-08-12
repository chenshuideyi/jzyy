package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.boss.ai.TitanWarden.TitanWardenAttackGoal;
import com.csdy.jzyy.entity.boss.ai.TitanWarden.RayTraceHelper;
import com.csdy.jzyy.particle.register.JzyyParticlesRegister;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class TitanWarden extends BossEntity implements GeoEntity {

    private boolean isEnraging = false;
    private boolean lastLockState;
    private int lockTicks;
    private int lockingTicks;
    private int remoteTicks;
    private int remotingTicks;

    private static final EntityDataAccessor<Boolean> DATA_IS_REMOTE =
            SynchedEntityData.defineId(TitanWarden.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING =
            SynchedEntityData.defineId(TitanWarden.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_LOCK =
            SynchedEntityData.defineId(TitanWarden.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_LOCKING =
            SynchedEntityData.defineId(TitanWarden.class, EntityDataSerializers.BOOLEAN);


    // --- 动画定义 ---
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.sniff");
    private static final RawAnimation ANGRY_IDLE_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.walk");

    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.look_at_target");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.attack");

    private static final RawAnimation REMOTE_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.sonic_boom");
    private static final RawAnimation LOCKING_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.roar");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent;
    public TitanWarden(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lastLockState = false;
        this.bossEvent = new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS); // 血条样式

    }


    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_REMOTE, false);
        this.entityData.define(DATA_IS_ATTACKING, false);
        this.entityData.define(DATA_IS_LOCKING, false);
        this.entityData.define(DATA_IS_LOCK, false);
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
            //锁定
            Optional<Player> player=getNearestPlayer(this,100);
            if (!this.isLock()&&player.isPresent()) {
                if (this.lockTicks < 200) {
                    this.lockTicks++;
                } else {
                    this.setLock(true);
                    this.startLocking();
                    this.setTarget(player.get());
                }
            } else if (!this.isLock()&& player.isEmpty()){
                this.lockTicks--;
            }
            //远程
            double attackReachSqr = this.getMeleeAttackRangeSqr();
            if (this.isLock()&&this.getTarget() != null&&!this.isAttacking()&&!this.isRemote()){
                if (this.remoteTicks<60){
                    this.remoteTicks++;
                }else {
                    this.startRemote();
                    this.remoteTicks = 0;
                }
            }
            if (this.remotingTicks == 1&&this.isRemote()&&this.getTarget()!=null) {
                RayTraceHelper.TraceResult result = RayTraceHelper.trace(this,this.getTarget(), attackReachSqr*4f,this.getBbWidth()*0.8f);
                for (BlockPos pos:result.blocks){
                    if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) >= 0) {
                        this.level().destroyBlock(pos,false);
                        if (this.level() instanceof ServerLevel serverLevel){
                            serverLevel.sendParticles(JzyyParticlesRegister.BIG_SONIC_BOOM.get(), pos.getX(),pos.getY(),pos.getZ(), 0, 0, 0, 0, 1);
                        }
                    }
                }
                for (LivingEntity living:result.entities) {
                    if (living != null &&living!=this) {
                        if (this.isLock()) {
                            float a = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                            living.hurt(this.damageSources().mobAttack(this), a);
                        } else {
                            this.doHurtTarget(living);
                        }
                    }
                }
            }

            if (this.lockingTicks>0)this.lockingTicks--;
            if (this.remotingTicks>0)this.remotingTicks--;
            if (this.lockingTicks<=0&&this.isLocking())this.setLocking(false);
            if (this.remotingTicks<=0&&this.isRemote())this.setRemote(false);
            if (this.getTarget() == null ||(this.getTarget() != null&&!this.getTarget().isAlive())){
                this.lockTicks=0;
                this.setLock(false);
            }
        }
    }
    public static Optional<Player> getNearestPlayer(LivingEntity living, double radius) {
        // 获取当前玩家的世界和位置
        ServerLevel level = (ServerLevel) living.level();
        AABB searchArea = new AABB(
                living.getX() - radius, living.getY() - radius, living.getZ() - radius,
                living.getX() + radius, living.getY() + radius, living.getZ() + radius
        );
        // 获取范围内的所有玩家
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, searchArea);
        // 按距离排序并返回最近的玩家
        return nearbyPlayers.stream().min(Comparator.comparingDouble(p -> p.distanceToSqr(living)));
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
        this.bossEvent.removeAllPlayers();
    }


    private static final UUID LOCK_SPEED_BOOST_UUID = UUID.fromString("41D1790F-2154-F941-26BD-0A4D929E6077");
    private static final AttributeModifier LOCK_SPEED_BOOST = new AttributeModifier(
            LOCK_SPEED_BOOST_UUID, "aaa_speed", 0.3D, AttributeModifier.Operation.MULTIPLY_BASE);

    private void manageSpeedModifier() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) return;
        if (this.isLock()) {
            if (!attributeInstance.hasModifier(LOCK_SPEED_BOOST)) {
                attributeInstance.addTransientModifier(LOCK_SPEED_BOOST);
            }
        } else {
            if (attributeInstance.hasModifier(LOCK_SPEED_BOOST)) {
                attributeInstance.removeModifier(LOCK_SPEED_BOOST);
            }
        }
    }


    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof Player player) {
            if ( !this.isEnraging&&!this.isLock()) {
                this.isEnraging = true;
                this.setLock(true);
                this.startLocking();
                this.setTarget(player);
            }
        }
        return super.hurt(source, amount);
    }

    // --- AI ---
    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new TitanWardenAttackGoal(this, 1.0D, true));

        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

    }

    // --- 动画 ---
    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<TitanWarden> state) {

        if (this.isLocking()) {
            return state.setAndContinue(LOCKING_ANIM);
        }

        if (this.isRemote()) {
            return state.setAndContinue(REMOTE_ANIM);
        }

        if (this.isAttacking()) {
            return state.setAndContinue(ATTACK_ANIM);
        }

        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }

        if (this.isLock()) {
            return state.setAndContinue(ANGRY_IDLE_ANIM);
        }else {return state.setAndContinue(IDLE_ANIM);}
    }

    // --- Getters and Setters for Synched Data ---
    public boolean isRemote() {
        return this.entityData.get(DATA_IS_REMOTE);
    }

    public void setRemote(boolean remote) {
        this.entityData.set(DATA_IS_REMOTE, remote);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACKING, attacking);
    }

    public boolean isLock() {
        return this.entityData.get(DATA_IS_LOCK);
    }

    public void setLock(boolean lock) {
        this.entityData.set(DATA_IS_LOCK, lock);
    }

    public boolean isLocking() {
        return this.entityData.get(DATA_IS_LOCKING);
    }

    public void setLocking(boolean locking) {
        this.entityData.set(DATA_IS_LOCKING, locking);
    }

    private void startLocking() {
        this.lockingTicks = 50;
        this.setLocking(true);
    }
    private void startRemote() {
        this.remotingTicks = 50;
        this.setRemote(true);
    }
    private double getMeleeAttackRangeSqr() {
        return (this.getBbWidth()*2) * (this.getBbWidth()*2);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.MAX_HEALTH, 500000000)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.ATTACK_SPEED, 1.0)
                .add(Attributes.FOLLOW_RANGE, 80.0);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.geoCache; }

    @Override
    public SoundEvent getBossMusic() { return JzyySoundsRegister.UMIYURI_KAITEITAN.get(); }

    @Override
    public void die(@NotNull DamageSource pSource) {
        if (isDeadOrDying()) {
            super.die(pSource);
        }
    }

    @Override
    public boolean isDeadOrDying() {
        return getHealth() <= 0;
    }

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

    private void updateDynamicName() {
        boolean currentState = this.isLock();
        // 只有当状态与上一tick不同时，才进行更新
        if (currentState != this.lastLockState) {
            Component newName;
            if (currentState) {
                newName = Component.translatable("entity.jzyy.titan_warden.lock");
            } else {
                newName = Component.translatable("entity.jzyy.titan_warden");
            }

            // 主动设置自定义名称，这将自动同步到客户端
            this.setCustomName(newName);
            // 确保名字总是可见的（可选，但推荐）
            this.setCustomNameVisible(true);

            // 更新记录的状态
            this.lastLockState = currentState;
        }
    }

}
