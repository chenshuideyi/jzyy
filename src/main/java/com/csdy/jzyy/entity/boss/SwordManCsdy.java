package com.csdy.jzyy.entity.boss;

import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.diadema.DiademaRegister;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class SwordManCsdy extends Monster implements GeoEntity {
    private static Diadema csdyWorld;

    public boolean isDead;
    private float oldHealth;
    private float lastHealth;
    private int updateTimer;
    private Entity.RemovalReason oldRemovalReason;
    private boolean damageTooHigh;

    private final ServerBossEvent bossEvent;
    public SwordManCsdy(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.entityData.set(DATA_HEALTH_ID,this.getMaxHealth());
        this.setMaxUpStep(0.6F);
        this.xpReward = 0;
        this.setPersistenceRequired();
        this.oldHealth = this.getHealth();
        this.bossEvent = (ServerBossEvent)(new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS // 血条样式
        )).setDarkenScreen(true); // 是否使屏幕变暗
        if (level.isClientSide) return;
        csdyWorld = JzyyDiademaRegister.CSDY_WORLD.get().CreateInstance(new FollowDiademaMovement(this));
    }

//    protected SoundEvent getAmbientSound() {
//        return JzyySoundsRegister.CUMULONIMBUS.get();
//    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        level.getLevel().sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                this.getX(),
                this.getY() + 1.0,
                this.getZ(),
                15,
                0.7, 0.7, 0.7,
                0.5
        );


        return super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.invulnerableTime = 0;
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            this.heal(10);
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.setWeatherParameters(0, 400, true, true);
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (isDeadOrDying()) {
            super.remove(reason);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        if (isDeadOrDying())
            super.onRemovedFromWorld();
    }

    @Override
    public void onClientRemoval() {
        if (isDeadOrDying())  {
            super.onClientRemoval();
        }
    }

    @Override
    public boolean isDeadOrDying() {
        return getHealth() <= 0;
    }

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

    @Override
    public boolean isNoAi() {
        return false;
    }

    @Override
    public void setNoAi(boolean noAi) {
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (!(source.getDirectEntity() instanceof Player)) return false;
        float realDamage = (float) Math.sqrt(damage);
        if (realDamage < 25F) {
            return false;
        }
        return super.hurt(source,damage);
    }

    @Override
    public void setHealth(float value) {
        float currentHealth = this.getHealth();
        float healthLoss = currentHealth - value;
        float threshold = 25.0f;
        if (healthLoss > threshold) {
            value = currentHealth - threshold;
        }

        super.setHealth(value);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return GeckoLibUtil.createInstanceCache(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 2);
        builder = builder.add(Attributes.MAX_HEALTH, 750);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 100.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16.0);
        return builder;
    }

}
