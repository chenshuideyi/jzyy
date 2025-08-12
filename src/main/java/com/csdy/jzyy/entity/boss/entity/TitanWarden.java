package com.csdy.jzyy.entity.boss.entity;

import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
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

import java.util.*;
import java.util.function.Predicate;

public class TitanWarden extends BossEntity implements GeoEntity {

    private boolean lastLockState;
    private int lockTicks;
    private int lockingTicks;
    private int remoteTicks;
    private int remotingTicks;

    public float multiple=5f;


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
    private static final RawAnimation ANGRY_IDLE_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.shiver");

    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.move");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.attack");

    private static final RawAnimation REMOTE_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.sonic_boom");
    private static final RawAnimation LOCKING_ANIM = RawAnimation.begin().thenLoop("animation.titan_warden.roar");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent;
    public TitanWarden(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lastLockState = false;
        this.setPersistenceRequired();
        this.bossEvent = new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.BLUE, // 血条颜色
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
    public void updateFluidHeightAndDoFluidPushing(Predicate<FluidState> shouldUpdate){

    }
    @Override
    public void tick() {
        super.tick();

        updateDynamicName();
        manageSpeedModifier();

        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.bossEvent.setName(this.getDisplayName());
            double w = this.getBbWidth()*this.multiple*0.5f;
            float b = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            //踩踏
            Vec3 vec3 = this.position();
            AABB aabb = new AABB(
                    vec3.x - w, vec3.y, vec3.z - w,
                    vec3.x + w, vec3.y + this.getBbHeight()*0.2f, vec3.z + w
            );
            List<LivingEntity> list=this.level().getEntitiesOfClass(LivingEntity.class,aabb);
            for (LivingEntity living:list) {
                if (living != this) {
                    living.invulnerableTime = 0;
                    living.hurt(LegacyDamageSource.mobAttack(this), b);
                    Vec3 vec = living.position().subtract(this.position()).normalize().scale(10);
                    living.push(vec.x, 4, vec.z);
                }
            }
            //锁定
            Optional<ServerPlayer> player=getNearestPlayer(this,this.level(),w*4f);
            if (!this.isLock()) {
                if (player.isEmpty()) {
                    this.lockTicks--;
                }else {
                    if (this.lockTicks < 200) {
                        this.lockTicks++;
                    } else {
                        this.setLock(true);
                        this.startLocking();
                        this.setTarget(player.get());
                    }
                }
            }
            //远程
            double attackReachSqr = this.getMeleeAttackRangeSqr();
            if (this.isLock()&&this.getTarget() != null&&!this.isAttacking()&&!this.isLocking()&&!this.isRemote()){
                if (this.remoteTicks<120){
                    this.remoteTicks++;
                }else {
                    this.startRemote();
                    this.remoteTicks = 0;
                }
            }
            if (this.remotingTicks == 68&&this.isRemote()&&this.getTarget()!=null) {
                float a = (float) this.position().subtract(this.getTarget().position()).length();
                RayTraceHelper.TraceResult result = RayTraceHelper.trace(this,this.getTarget(), a+attackReachSqr*2f,w);
//                RayTraceHelper.TraceResult result1 = RayTraceHelper.tracea(this,this.getTarget(), a+attackReachSqr*2f,w,w);
//                for (BlockPos pos:result1.blocks){
//                    BlockState state = level.getBlockState(pos);
//                    if (!state.isAir()&&state.getDestroySpeed(this.level(), pos) >= 0) {
//                        this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
//                    }
//                }
                for (BlockPos pos:result.blocks){
                    if (this.level() instanceof ServerLevel serverLevel){
                        serverLevel.sendParticles(JzyyParticlesRegister.BIG_SONIC_BOOM.get(), pos.getX(),pos.getY(),pos.getZ(), 0, 0, 0, 0, 1);
                    }
                }
                for (LivingEntity living:result.entities) {
                    if (living != null &&living!=this) {
                        if (this.isLock()) {
                            living.hurt(LegacyDamageSource.mobAttack(this).setBypassArmor().setBypassMagic().setBypassEnchantment().setBypassInvul().setBypassInvulnerableTime().setBypassShield(), b*0.01f*a);
                        }
                        if (this.level() instanceof ServerLevel serverLevel) {
                            serverLevel.playSound(null, living.getX(), living.getY(), living.getZ(),
                                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS,
                                    1f, 1f);
                        }
                    }
                }
            }
            float r = (float) (attackReachSqr*0.25f);
            AABB aabb1 = new AABB(
                    vec3.x - r, vec3.y, vec3.z - r,
                    vec3.x + r, vec3.y + this.getBbHeight()*0.4f, vec3.z + r
            );
            List<LivingEntity> list1=this.level().getEntitiesOfClass(LivingEntity.class,aabb1);
            if (this.lockingTicks>0) {
                this.lockingTicks--;
                if (this.lockingTicks==95) {
                    for (LivingEntity living : list1) {
                        if (living != this) {
                            living.invulnerableTime = 0;
                            living.hurt(LegacyDamageSource.mobAttack(this), b * 0.1f);
                            Vec3 vec = living.position().subtract(this.position()).normalize().scale(10);
                            living.push(vec.x, 4, vec.z);
                        }
                    }
                }
            }
            if (this.remotingTicks>0)this.remotingTicks--;
            if (this.lockingTicks<=0&&this.isLocking())this.setLocking(false);
            if (this.remotingTicks<=0&&this.isRemote())this.setRemote(false);
            if (this.isLock()&&(this.getTarget() == null ||(this.getTarget() != null&&!this.getTarget().isAlive()))){
                this.lockTicks=0;
                this.setLock(false);
            }

            float h=this.getMaxHealth()-this.getHealth();
            float h1=this.getMaxHealth()*0.002f;
            if (h>0){
                this.heal(Math.min(h1, h));
            }
        }
    }
    private Optional<ServerPlayer> getNearestPlayer(LivingEntity living,Level level, double radius) {
        AABB searchArea = new AABB(
                living.getX() - radius, living.getY() - radius, living.getZ() - radius,
                living.getX() + radius, living.getY() + radius, living.getZ() + radius
        );
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, searchArea);
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
            LOCK_SPEED_BOOST_UUID, "aaa_speed", 1.2, AttributeModifier.Operation.MULTIPLY_BASE);

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
            if (!this.isLock()) {
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
        this.lookControl = new LookControl(this) {
            @Override
            public void tick() {
                // 让头部平滑看向目标
                if (this.getWantedY() != 0 || this.getWantedX() != 0|| this.getWantedZ() != 0) {
                    super.tick();
                }
            }
        };
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
        this.lockingTicks = 160;
        this.setLocking(true);
    }
    private void startRemote() {
        this.remotingTicks = 120;
        this.setRemote(true);
    }
    private double getMeleeAttackRangeSqr() {
        return (this.getBbWidth()*3f*this.multiple) * (this.getBbWidth()*3f*this.multiple);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.8)
                .add(Attributes.MAX_HEALTH, 500000000)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.ATTACK_SPEED, 1)
                .add(Attributes.FOLLOW_RANGE, 30000)
                .add(Attributes.ATTACK_KNOCKBACK, 15);
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
