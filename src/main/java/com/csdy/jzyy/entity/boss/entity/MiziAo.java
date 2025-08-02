package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.boss.ai.mizi.MiziMeleeGoal;
import com.csdy.jzyy.entity.boss.ai.PersistentHurtByTargetGoal;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
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

public class MiziAo extends BossEntity implements GeoEntity {

    private boolean ATTACKING;
    private static final int rating = 15160;
    private static final int perfect = 101;
    public static boolean 出警 = false;

    public MiziAo(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public boolean isAttacking() {
        return this.ATTACKING;
    }

    public void setAttacking(boolean attacking) {
        this.ATTACKING = attacking;
    }

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.attack");
    private static final RawAnimation DRINK_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.drink");


    private static final RawAnimation ANGRY_STAND_ANIM = RawAnimation.begin().thenLoop("animation.mizi_ao.angry_stand");

    @Override
    public SoundEvent getBossMusic() {
        return JzyySoundsRegister.UMIYURI_KAITEITAN.get();
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private PlayState idleOrStandAnimController(AnimationState<MiziAo> state) {
        if (出警) {
            return state.setAndContinue(ANGRY_STAND_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (!(source.getDirectEntity() instanceof Player player)) return false;
        boolean isCritical = player.fallDistance > 0.0F &&  // 玩家正在下落
                !player.onGround() &&          // 玩家不在地面
                !player.isSwimming() &&        // 玩家不在游泳
                !player.isPassenger() &&       // 玩家没有骑乘坐骑
                !player.isDiscrete() &&        // 玩家没有潜行
                player.getAttackStrengthScale(0.5F) > 0.9F; // 攻击蓄力充足
        if (isCritical && !出警){
            出警 = true;
        }

        return super.hurt(source,damage);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new MiziMeleeGoal(this, 1.0D, false,this)); // 2: 近战攻击

        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));

        // 目标选择器 (targetSelector) - 修改后的版本
        this.targetSelector.addGoal(1, new PersistentHurtByTargetGoal(this)); // 自定义的持续仇恨目标
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
        return false;
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
