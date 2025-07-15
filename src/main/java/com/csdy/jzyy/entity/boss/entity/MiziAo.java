package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MiziAo extends BossEntity implements GeoEntity {


    private static final int rating = 15160;
    private static final int perfect = 101;
    private final boolean 出警 = false;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.idle");
    private static final RawAnimation STAND_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.stand");

    public MiziAo(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public SoundEvent getBossMusic() {
        return JzyySoundsRegister.UMIYURI_KAITEITAN.get();
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private PlayState idleOrStandAnimController(AnimationState<MiziAo> state) {
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 5, this::idleOrStandAnimController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean isNoAi() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, rating);
        builder = builder.add(Attributes.ARMOR, rating);
        builder = builder.add(Attributes.ATTACK_DAMAGE, perfect);
        builder = builder.add(Attributes.ATTACK_SPEED, 1.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }
}
