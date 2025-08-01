
package com.csdy.jzyy.entity.monster.entity;


import com.csdy.jzyy.entity.boss.ai.PersistentHurtByTargetGoal;
import com.csdy.jzyy.entity.monster.ai.HJMEntityGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
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


public class HJMEntity extends Monster implements GeoEntity {

	private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
	private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("attack");
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.PROGRESS);
	private boolean ATTACKING;

	public HJMEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		setMaxUpStep(0.6f);
		xpReward = 0;
		setNoAi(false);
		refreshDimensions();
	}

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	public boolean isAttacking() {
		return this.ATTACKING;
	}

	public void setAttacking(boolean attacking) {
		this.ATTACKING = attacking;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return geoCache;
	}

	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "attack", 0, this::attackAnimController));

		controllers.add(new AnimationController<>(this, "walk", 0, this::moveAnimController));

		controllers.add(new AnimationController<>(this, "idle", 0, this::idleAnimController));
	}


	private PlayState attackAnimController(AnimationState<HJMEntity> state) {
		if (isAttacking())
			return state.setAndContinue(ATTACK_ANIM);
		return PlayState.STOP;
	}

	private PlayState moveAnimController(AnimationState<HJMEntity> state) {
		if (state.isMoving())
			return state.setAndContinue(WALK_ANIM);

		return PlayState.STOP;
	}

	private PlayState idleAnimController(AnimationState<HJMEntity> state) {
		return state.setAndContinue(IDLE_ANIM);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new HJMEntityGoal(this, 1.0D, false, this)); // 2: 近战攻击
		this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new PersistentHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
		builder = builder.add(Attributes.MAX_HEALTH, 999);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 1);
		builder = builder.add(Attributes.ATTACK_SPEED, 1);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 6);
		builder = builder.add(Attributes.FOLLOW_RANGE, 12);
		return builder;
	}
	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);

	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
	}
	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
	}
}

