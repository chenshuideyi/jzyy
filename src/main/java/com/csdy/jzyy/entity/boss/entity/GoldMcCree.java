package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.boss.ai.gold_mccree.RangedKeepDistanceAndRunGoal;
import com.csdy.jzyy.shader.BlackFogEffect;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

public class GoldMcCree extends BossEntity implements GeoEntity {

    private boolean firstHurt;
    private final ServerBossEvent bossEvent;
    private int transitionTimer = 0;

    private int attackingTimer = 0;
    private int movingTimer = 0;
    private static final int STATE_DURATION = 40;

    public GoldMcCree(EntityType<? extends BossEntity> type, Level level) {
        super(type, level);
        this.firstHurt = false;
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
        this.setMaxUpStep(0.6F);
        this.xpReward = 0;
        this.setPersistenceRequired();
    }

    @Override
    public void tick() {
        super.tick();

        // 处理转阶段计时
        if (isTransitioning() && transitionTimer > 0) {
            transitionTimer--;
            if (transitionTimer <= 0) {
                endPhaseTransition();
                setPhase(getPhase() + 1);
                setJoinBattle(false);
            }
        }

        if (attackingTimer > 0) {
            attackingTimer--;
            if (attackingTimer <= 0) {
                this.setAttacking(false);
            }
        }

        // 处理移动状态计时
        if (movingTimer > 0) {
            movingTimer--;
            if (movingTimer <= 0) {

            }
        }


        this.heal(10000f);

        if (!this.level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level;
            long nightTime = 18000L;
            serverLevel.setDayTime(nightTime);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (isInvulnerableTo(source) || isDeadOrDying()) return false;

        if (!this.firstHurt) {
            this.firstHurt = true;
            setJoinBattle(true);
            startPhaseTransition(2);
        }

        float healthRatio = this.getHealth() / this.getMaxHealth();

        // 对数盾核心算法
        float logDamage = (float) Math.log10(Math.max(1, damage));

        float processedDamage;
        if (logDamage <= 7.0f) { // 1000万以下伤害
            processedDamage = logDamage * 20000f;
        } else if (logDamage <= 10.0f) { // 100亿以下伤害
            processedDamage = 140000f + (logDamage - 7.0f) * 1000f;
        } else { // 100亿以上伤害
            processedDamage = 143000f + (logDamage - 10.0f) * 100f;
        }

        // 血量影响系数
        float healthMultiplier = 0.9f + (1 - healthRatio) * 0.2f;

        float realDamage = processedDamage * healthMultiplier;

        // 实际应用伤害到实体
        return super.hurt(source, realDamage); // 返回true并应用伤害
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
        BlackFogEffect.SetEnableTo(player, true);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
        BlackFogEffect.SetEnableTo(player, false);
    }

    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(GoldMcCree.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING =
            SynchedEntityData.defineId(GoldMcCree.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_JOIN_BATTLE =
            SynchedEntityData.defineId(GoldMcCree.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_TRANSITIONING =
            SynchedEntityData.defineId(GoldMcCree.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_BLINKING =
            SynchedEntityData.defineId(GoldMcCree.class, EntityDataSerializers.BOOLEAN);

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PHASE, 1);
        this.entityData.define(DATA_IS_ATTACKING, false);
        this.entityData.define(DATA_JOIN_BATTLE, false);
        this.entityData.define(DATA_IS_TRANSITIONING, false);
        this.entityData.define(DATA_IS_BLINKING, false);
    }

    public int getPhase() {
        return this.entityData.get(DATA_PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(DATA_PHASE, phase);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACKING, attacking);
    }

    public boolean isBlinking() {
        return this.entityData.get(DATA_IS_BLINKING);
    }

    public void setBlinking(boolean blinking) {
        this.entityData.set(DATA_IS_BLINKING, blinking);
    }

    public boolean isJoinBattle() {
        return this.entityData.get(DATA_JOIN_BATTLE);
    }

    public void setJoinBattle(boolean battle) {
        this.entityData.set(DATA_JOIN_BATTLE, battle);
    }

    public boolean isTransitioning() {
        return this.entityData.get(DATA_IS_TRANSITIONING);
    }

    public void setTransitioning() {
        this.entityData.set(DATA_IS_TRANSITIONING, true);
    }

    public void endPhaseTransition() {
        this.entityData.set(DATA_IS_TRANSITIONING, false);
    }

    private void startPhaseTransition(int newPhase) {
        System.out.println("开始转阶段: " + getPhase() + " -> " + newPhase);

        // 设置转阶段状态
        setTransitioning();
        transitionTimer = 30; // 2秒

        // 停止所有AI和移动
        this.goalSelector.getRunningGoals().forEach(goal -> goal.stop());
        this.targetSelector.getRunningGoals().forEach(goal -> goal.stop());
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.setAttacking(false);

    }

    public void setAttackingWithDuration(boolean attacking) {
        this.setAttacking(attacking);
        if (attacking) {
            attackingTimer = STATE_DURATION;
        } else {
            attackingTimer = 0;
        }
    }

    public void setMovingWithDuration(boolean moving) {
        if (moving) {
            movingTimer = STATE_DURATION;
        } else {
            movingTimer = 0;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new RangedKeepDistanceAndRunGoal(this, 2.5D, 9, 18, 1, 32));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // 确保这些动画名称与你的geo文件中的动画名称完全一致
    private static final RawAnimation PHASE_1 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase1");
    private static final RawAnimation PHASE_2 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase2");
    private static final RawAnimation PHASE_3 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase3");
    private static final RawAnimation PHASE_4 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase4");

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.idel"); // 注意拼写
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.walk");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.run");
    private static final RawAnimation BLINK_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.blink");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.attack");
    private static final RawAnimation BATTLE_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.battle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "main_controller", 0, this::mainAnimationHandler));

        controllers.add(new AnimationController<>(this, "move_controller", 1, this::moveHandler));

        controllers.add(new AnimationController<>(this, "phase_controller", 2, this::phaseState));

        controllers.add(new AnimationController<>(this, "battle_controller", 2, this::BattleHandler));
    }

    // 修改动画控制器
    private PlayState mainAnimationHandler(AnimationState<GoldMcCree> state) {
        int phase = getPhase();

        // 阶段1：只播放idle
        if (phase == 1) {
            return state.setAndContinue(IDLE_ANIM);
        }

        // 转阶段时停止所有动作
        if (this.isTransitioning()) {
            return PlayState.STOP;
        }

        // 阶段2-4：正常行为
        boolean isMoving = state.isMoving();
        boolean isAttacking = this.isAttacking();
        boolean isBlinking = this.isBlinking();

        if (isBlinking) {
            return state.setAndContinue(BLINK_ANIM);
        } else if (isMoving && isAttacking) {
            return state.setAndContinue(ATTACK_ANIM);
        } else if (isMoving) {
            return state.setAndContinue(RUN_ANIM);
        } else if (isAttacking) {
            return state.setAndContinue(ATTACK_ANIM);
        } else {
            return state.setAndContinue(RUN_ANIM);
        }
    }

    private PlayState BattleHandler(AnimationState<GoldMcCree> state) {
        if (this.isJoinBattle()) {
            return state.setAndContinue(BATTLE_ANIM);
        }
        return PlayState.STOP;

    }

    private PlayState moveHandler(AnimationState<GoldMcCree> state) {
        if (state.isMoving()) {
            return state.setAndContinue(RUN_ANIM);
        }
        return PlayState.STOP;

    }

    private PlayState phaseState(AnimationState<GoldMcCree> state) {
        int phase = getPhase();
        return switch (phase) {
            case 1 -> state.setAndContinue(PHASE_1);
            case 2 -> state.setAndContinue(PHASE_2);
            case 3 -> state.setAndContinue(PHASE_3);
            case 4 -> state.setAndContinue(PHASE_4);
            default -> PlayState.STOP;
        };
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder = builder.add(Attributes.MAX_HEALTH, 3293586226.0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 6400.0);
        builder = builder.add(Attributes.ATTACK_SPEED, 20.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 128);
        return builder;
    }
}
