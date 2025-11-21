package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.shader.BlackFogEffect;
import com.csdy.jzyy.shader.BloodSkyEffect;
import com.csdy.tcondiadema.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
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

    private final ServerBossEvent bossEvent;
    public GoldMcCree(EntityType<? extends BossEntity> type, Level level) {
        super(type, level);
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

        if (!this.level.isClientSide) {
            // 服务器端执行
            ServerLevel serverLevel = (ServerLevel) this.level;
            long nightTime = 18000L; // 午夜时间
            serverLevel.setDayTime(nightTime);
        }
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
            SynchedEntityData.defineId(SwordManCsdy.class, EntityDataSerializers.INT);

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PHASE, 1);;
    }

    public int getPhase(){
        return this.entityData.get(DATA_PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(DATA_PHASE, phase);
    }

    private static final RawAnimation PHASE_1 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase1");
    private static final RawAnimation PHASE_2 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase1");
    private static final RawAnimation PHASE_3 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase1");
    private static final RawAnimation PHASE_4 = RawAnimation.begin().thenLoop("animation.gold_mccree.phase1");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.idel");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.gold_mccree.walk");


    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "state_controller", 0, this::stateMachine));
    }

    private PlayState stateMachine(AnimationState<GoldMcCree> state) {
        int phase = getPhase();

        switch(phase) {
            case 1:
                if (state.isMoving()) {
                    return state.setAndContinue(WALK_ANIM);
                } else {
                    return state.setAndContinue(IDLE_ANIM); // 转酒杯待机
                }
            case 2:
                // Phase 2的逻辑
                return state.setAndContinue(PHASE_2);
            case 3:
                // Phase 3的逻辑
                return state.setAndContinue(PHASE_3);
            case 4:
                // Phase 4的逻辑
                return state.setAndContinue(PHASE_4);
            default:
                return PlayState.STOP;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 499999.0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 6400.0);
        builder = builder.add(Attributes.ATTACK_SPEED, 20.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 128);
        return builder;
    }
}
