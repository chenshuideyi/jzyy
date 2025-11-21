package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.boss.BossMusic;
import com.csdy.jzyy.entity.boss.ai.CsdyMeleeGoal;
import com.csdy.jzyy.entity.boss.ai.PersistentHurtByTargetGoal;
import com.csdy.jzyy.shader.BloodSkyEffect;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
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

import javax.annotation.Nullable;
import java.util.List;

import static com.csdy.jzyy.entity.Util.forceHurt;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionSeverance;


public class SwordManCsdy extends BossEntity implements GeoEntity {

    // 动画定义
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.model.stand");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.model.walk");

    // 实体数据定义
    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING =
            SynchedEntityData.defineId(SwordManCsdy.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_REAL =
            SynchedEntityData.defineId(SwordManCsdy.class, EntityDataSerializers.BOOLEAN);

    // 自定义血量系统
    private float currentHealth = 15000000.0f;
    private float maxHealth = 15000000.0f;
    private boolean isDying = false;

    // 战利品表
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("jzyy", "entities/sword_man_csdy");

    // 其他字段
    private CsdyMeleeGoal meleeGoal;
    private int attackBehaviorCooldown = 0;
    private AnimationController<SwordManCsdy> mainAnimationController;
    private static Diadema csdyWorld;
    private transient BossMusic clientBossMusicInstance;
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
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
        this.setMaxUpStep(0.6F);
        this.xpReward = 0;
        this.setPersistenceRequired();
        this.oldHealth = this.getHealth();

        if (level.isClientSide) return;
        csdyWorld = JzyyDiademaRegister.CSDY_WORLD.get().CreateInstance(new FollowDiademaMovement(this));

        // 初始化时同步血量显示
        syncHealthToNative();
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_ATTACKING, false);
        this.entityData.define(DATA_IS_REAL, false);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACKING, attacking);
    }

    public boolean isReal() {
        return this.entityData.get(DATA_IS_REAL);
    }

    public void setReal(boolean real) {
        this.entityData.set(DATA_IS_REAL, real);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        if (name != null && name.getString().contains("沉睡的艺") && !isReal()) {
            this.currentHealth = this.getMaxHealth();
            setReal(true);
            syncHealthToNative();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // 使用自定义血量更新血条 - 使用超类的bossEvent
            this.bossEvent.setProgress(this.currentHealth / this.getMaxHealth());
            this.bossEvent.setName(this.getDisplayName());

            // 每20tick设置天气
            if (this.tickCount % 20 == 0) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.setWeatherParameters(0, 400, true, true);
            }

            // 死亡处理
            if (this.currentHealth <= 0 && !isDying) {
                handleDeath();
            }
        }

        this.invulnerableTime = 0;

        // 持续同步血量显示
        if (this.tickCount % 10 == 0) {
            syncHealthToNative();
        }

        // 客户端音乐停止处理
        if (this.level().isClientSide && (isDying || this.currentHealth <= 0)) {
            stopBossMusic();
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.bossEvent.removeAllPlayers();
        // 停止音乐
        if (!this.level().isClientSide) {
            // 服务端移除血天效果
            if (this.level() instanceof ServerLevel serverLevel) {
                for (ServerPlayer player : serverLevel.players()) {
                    BloodSkyEffect.SetEnableTo(player, false);
                }
            }
        } else {
            // 客户端停止音乐
            stopBossMusic();
        }
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
        BloodSkyEffect.SetEnableTo(player, true);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
        BloodSkyEffect.SetEnableTo(player, false);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (isReal() || isDying) return false;

        float realDamage = (float) Math.sqrt(damage) * 2;

        if (realDamage < 100f) {
            return false;
        }

        // 计算血量比例减伤（血量越低减伤越多）
        float healthRatio = this.currentHealth / this.getMaxHealth(); // 假设有maxHealth字段
        float damageReduction = (1 - healthRatio) * 0.9f; // 最多减伤90%
        realDamage = realDamage * (1 - damageReduction);

        this.currentHealth -= realDamage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }

        teleportToAttacker(source);

        if (this.currentHealth <= 0 && !isDying) {
            handleDeath();
        }

        return true;
    }

    private void teleportToAttacker(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null || attacker == this) return;

        double x = attacker.getX() + (random.nextDouble() - 0.5) * 1.2;
        double y = attacker.getY() + random.nextInt(2);
        double z = attacker.getZ() + (random.nextDouble() - 0.5) * 1.2;

        this.teleportTo(x, y, z);
    }

    /**
     * 处理死亡逻辑
     */
    private void handleDeath() {
        if (isDying) return;

        if (this.level() == null || this.level().getServer() == null) {
            // 服务器已关闭，跳过死亡处理
            return;
        }

        isDying = true;

        DamageSource deathSource = this.damageSources().generic();
        super.die(deathSource);

        this.dropAllDeathLoot(deathSource);

        super.setHealth(0);
    }

    @Override
    public void dropAllDeathLoot(DamageSource pDamageSource) {
        if (this.currentHealth > 0 || !isDying) return;
        super.dropAllDeathLoot(pDamageSource);
    }

    @Override
    protected @NotNull ResourceLocation getDefaultLootTable() {
        return LOOT_TABLE;
    }

    // ========== 血量系统防护 ==========

    /**
     * 同步自定义血量到原生系统（仅用于显示）
     */
    private void syncHealthToNative() {
        super.setHealth(Math.max(0.1f, this.currentHealth));
    }

    @Override
    public float getHealth() {
        return this.currentHealth;
    }

    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setHealth(float value) {
        if (isReal() || isDying) return;

        // 只允许增加血量，不允许减少
        if (value > this.currentHealth) {
            this.currentHealth = Math.min(value, this.getMaxHealth());
            syncHealthToNative();
        }
    }

    @Override
    public void setAbsorptionAmount(float amount) {
        super.setAbsorptionAmount(0);
    }

    @Override
    public void heal(float amount) {
        if (!isReal() && !isDying) {
            this.currentHealth = Math.min(this.currentHealth + amount, this.getMaxHealth());
            syncHealthToNative();
        }
    }

    @Override
    public boolean isDeadOrDying() {
        return isDying || this.currentHealth <= 0;
    }

    @Override
    public boolean isAlive() {
        return !isDying && this.currentHealth > 0;
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (this.currentHealth <= 0 && !isDying) {
            handleDeath();
        }
    }

    @Override
    public void kill() {
        csdySay("可悲的尝试");
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.KILLED && !isDying) {
            csdySay("苟延残喘又不肯认输的样子真丑陋啊");
            return;
        }
        super.remove(reason);
    }

    // ========== 动画系统 ==========

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::mainAnimController));
    }

    private PlayState mainAnimController(AnimationState<SwordManCsdy> state) {
        if (state.isMoving() || this.getTarget() != null || isAttacking()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // ========== 属性注册 ==========

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 2.2);
        builder = builder.add(Attributes.MAX_HEALTH, 499999.0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 640000.0);
        builder = builder.add(Attributes.ATTACK_SPEED, 20.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 128);
        return builder;
    }

    // ========== 其他方法 ==========

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new CsdyMeleeGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new PersistentHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
    }

    @Override
    public SoundEvent getBossMusic() {
        return JzyySoundsRegister.GIRL_A.get();
    }

    @Override
    public void tickDeath() {
        ++this.deathTime;

        // 死亡后继续战斗10秒（200 ticks = 10秒）
        if (this.deathTime >= 20 && this.deathTime < 200) { // 第1秒到第10秒
            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                // 对附近半径8格的生物造成伤害
                damageNearbyEntities(serverLevel);
            }
        }

        if (this.deathTime >= 200 && !this.level().isClientSide() && !this.isRemoved()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (ServerPlayer player : serverLevel.players()) {
                    BloodSkyEffect.SetEnableTo(player, false);
                }
            }
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    // 对附近生物造成伤害的方法
    private void damageNearbyEntities(ServerLevel level) {
        // 获取boss位置
        Vec3 bossPos = this.position();

        // 查找半径8格内的所有生物（除了自己）
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(bossPos.x - 8, bossPos.y - 8, bossPos.z - 8,
                        bossPos.x + 8, bossPos.y + 8, bossPos.z + 8),
                entity -> entity != this && entity.isAlive()
        );

        // 对每个生物造成伤害
        for (LivingEntity target : nearbyEntities) {
            // 造成伤害，可以根据需要调整伤害值

            int currentLevel = target.hasEffect(JzyyEffectRegister.DEEP_WOUND.get()) ?
                    target.getEffect(JzyyEffectRegister.DEEP_WOUND.get()).getAmplifier() : -1;

            int newLevel = currentLevel + 1;
            float damage = 1 + newLevel;

            forceHurt(target, this.damageSources().mobAttack(this), damage * 20);
            reflectionSeverance(target,target.getHealth() - 100);
            target.addEffect(new MobEffectInstance(JzyyEffectRegister.DEEP_WOUND.get(), 20 * 15, newLevel));

        }


    }

    private void csdySay(String line) {
        MinecraftServer server = this.level().getServer();
        if (server != null) {
            for (ServerPlayer onlinePlayer : server.getPlayerList().getPlayers()) {
                onlinePlayer.displayClientMessage(
                        Component.translatable(line).withStyle(ChatFormatting.RED),
                        false
                );
            }
        }
    }

    // 用于实体注册的工厂方法
    public static SwordManCsdy create(EntityType<SwordManCsdy> entityType, Level level) {
        return new SwordManCsdy(entityType, level);
    }
}
