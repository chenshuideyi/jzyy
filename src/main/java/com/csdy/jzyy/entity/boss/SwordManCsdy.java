package com.csdy.jzyy.entity.boss;

import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.entity.boss.ai.CsdyMeleeGoal;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimationState;

import javax.annotation.Nullable;

public class SwordManCsdy extends BossEntity implements GeoEntity {

    // 更新 RawAnimation 定义以匹配你的JSON文件
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.model.stand");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.model.walk");


    private CsdyMeleeGoal meleeGoal; // 持有对近战Goal的引用
    private int attackBehaviorCooldown = 0; // 控制攻击行为（动画+伤害）的整体冷却
    private AnimationController<SwordManCsdy> mainAnimationController; // 主动画控制器

    private static Diadema csdyWorld;

    private transient BossMusic clientBossMusicInstance; // transient 防止序列化，客户端专用
    private boolean musicStarted = false;

    public boolean isDead;
    private float oldHealth;
    private float lastHealth;
    private int updateTimer;
    private Entity.RemovalReason oldRemovalReason;
    private boolean damageTooHigh;

    private final ServerBossEvent bossEvent;
    public SwordManCsdy(EntityType<? extends BossEntity> type, Level level) {
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


    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
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

         if (!this.level().isClientSide && this.bossEvent != null) {
             this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
         }

        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.setWeatherParameters(0, 400, true, true);
        }
        this.invulnerableTime = 0;

    }

    // 确保在Boss实体被移除或死亡时，音乐也停止
    @Override
    public void onRemovedFromWorld() {
        if (isDeadOrDying()) {
            super.onRemovedFromWorld(); // 调用父类很重要
            if (level().isClientSide()) {
                if (clientBossMusicInstance != null) {
                    Minecraft.getInstance().getSoundManager().stop(clientBossMusicInstance);
                }
                musicStarted = false;
                clientBossMusicInstance = null; // 清理引用
            }
        }
    }

    @Override
    public void die(DamageSource pSource) {
        if (isDeadOrDying()) {
            super.die(pSource); // 调用父类
            if (level().isClientSide()) {
                if (clientBossMusicInstance != null) {
                    Minecraft.getInstance().getSoundManager().stop(clientBossMusicInstance);
                }
                musicStarted = false;
                clientBossMusicInstance = null; // 清理引用
            }
        }
    }

    @Override
    public SoundEvent getBossMusic() {
        return JzyySoundsRegister.GIRL_A.get();
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (isDeadOrDying()) {
            super.remove(reason);
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
    protected void registerGoals() {
        super.registerGoals(); // 可选，如果基类有重要行为需要保留

        // 行为选择器 (goalSelector)
//        this.goalSelector.addGoal(0, new FlyToTargetWhenStuckOrInLiquidGoal(this, 6D));
        this.goalSelector.addGoal(0, new CsdyMeleeGoal(this, 1.0D, false)); // 2: 近战攻击
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));

        // 目标选择器 (targetSelector)
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this)); // 1: 被攻击时，将攻击者设为目标
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true)); // 2: 寻找最近的玩家作为目标
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    private PlayState walkAnimController(AnimationState<SwordManCsdy> state) {
        if (!state.isMoving() && this.getHealth() ==  this.getMaxHealth())
            return state.setAndContinue(IDLE_ANIM);
        else return state.setAndContinue(WALK_ANIM);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }



    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 3.2);
        builder = builder.add(Attributes.MAX_HEALTH, 750);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 400.0);
        builder = builder.add(Attributes.ATTACK_SPEED, 10.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

}
