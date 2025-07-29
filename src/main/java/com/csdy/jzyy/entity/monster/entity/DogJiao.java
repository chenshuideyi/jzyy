package com.csdy.jzyy.entity.monster.entity;

import com.csdy.jzyy.entity.boss.ai.PersistentHurtByTargetGoal;
import com.csdy.jzyy.entity.monster.ai.DogJiaoMeleeGoal;
import lombok.Getter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DogJiao extends Monster implements GeoEntity {

    private boolean ATTACKING;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao.rush");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao.attack");

    public DogJiao(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        // 1. Attack 最先检查（最高优先级）
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackAnimController));
        // 2. Walk 其次
        controllers.add(new AnimationController<>(this, "move", 0, this::moveAnimController));
        // 3. Idle 最后（最低优先级）
        controllers.add(new AnimationController<>(this, "idle", 0, this::idleAnimController));
    }



    private PlayState attackAnimController(AnimationState<DogJiao> state) {
        if (isAttacking())
            return state.setAndContinue(ATTACK_ANIM);

        return PlayState.STOP;
    }

    private PlayState moveAnimController(AnimationState<DogJiao> state) {

        if (state.isMoving())
            return state.setAndContinue(WALK_ANIM);

        return PlayState.STOP;
    }

    private PlayState idleAnimController(AnimationState<DogJiao> state) {
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new DogJiaoMeleeGoal(this, 1.0D, false,this)); // 2: 近战攻击
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new PersistentHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.8);
        builder = builder.add(Attributes.MAX_HEALTH, 12);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
        builder = builder.add(Attributes.ATTACK_SPEED, 2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

}
