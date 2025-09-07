package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.entity.boss.ai.PersistentHurtByTargetGoal;
import com.csdy.jzyy.entity.boss.ai.dog_jiao_jiao_jiao.DogJiaoJiaoJiaoAttackGoal;
import com.csdy.jzyy.entity.monster.ai.DogJiaoMeleeGoal;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

import static com.csdy.jzyy.entity.monster.event.NightDogJiaoSummon.trySpawnZombiesNearPlayer;

public class DogJiaoJiaoJiao extends BossEntity implements GeoEntity {

    private UUID lastHatedUUID = null;
    private int hateCooldown = 0;
    private static final int HATE_COOLDOWN = 4000; // 200秒仇恨记忆，哈哈你们有的爽了



    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao_jiao_jiao.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao_jiao_jiao.walk");
    private static final RawAnimation SCREAM_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao_jiao_jiao.scream"); // 添加吼叫动画
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.dog_jiao_jiao_jiao.attack");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> DATA_IS_SCREAMING =
            SynchedEntityData.defineId(DogJiaoJiaoJiao.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACK =
            SynchedEntityData.defineId(DogJiaoJiaoJiao.class, EntityDataSerializers.BOOLEAN);

    // 添加计时器变量
    public int screamTicks = 0;
    private int screamCooldown = 0;
    private boolean hasScreamedInitially = false;

    private final ServerBossEvent bossEvent;
    public DogJiaoJiaoJiao(EntityType<? extends BossEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.RED, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS); // 血条样式
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_SCREAMING, false);
        this.entityData.define(DATA_IS_ATTACK, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    public boolean isScreaming() {
        return this.entityData.get(DATA_IS_SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        this.entityData.set(DATA_IS_SCREAMING, screaming);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACK);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACK, attacking);
    }

    // 添加吼叫方法
    public void startScreaming() {
        if (this.isAttacking()) {
            return; // 如果正在攻击，不进行吼叫
        }

        this.setScreaming(true);
        this.screamTicks = 40;

        // 对16×16范围内的玩家造成40点伤害
        if (!this.level().isClientSide) {
            // 获取实体所在位置
            double centerX = this.getX();
            double centerY = this.getY();
            double centerZ = this.getZ();

            trySpawnZombiesNearPlayer((ServerLevel) this.level(),this,12);

            // 定义伤害范围（16×16方块）
            AABB damageArea = new AABB(
                    centerX - 8.0, centerY - 4.0, centerZ - 8.0,
                    centerX + 8.0, centerY + 4.0, centerZ + 8.0
            );

            // 获取范围内的所有玩家
            List<Player> playersInRange = this.level().getEntitiesOfClass(
                    Player.class, damageArea, Entity::isAlive
            );

            // 对每个玩家造成伤害
            for (Player player : playersInRange) {
                // 造成20点伤害，伤害来源是这个实体
                player.hurt(this.damageSources().mobAttack(this), 40.0F);

                // 可选：添加击退效果
                player.knockback(1.0F,
                        player.getX() - centerX,
                        player.getZ() - centerZ);

                // 可选：添加屏幕抖动或其他效果
                if (player instanceof ServerPlayer serverPlayer) {
                    JzyySyncing.CHANNEL.send(
                            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                            new PlaySoundPacket(player.getEyePosition(), JzyySoundsRegister.SCREAM1.get().getLocation(),1,1)
                    );
                }
            }



        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.bossEvent.setName(this.getDisplayName());

            manageHateMemory();

            // 处理吼叫逻辑 - 只在非攻击状态下进行
            if (this.screamTicks > 0 && !this.isAttacking()) {
                this.screamTicks--;

                // 吼叫结束时
                if (this.screamTicks <= 0) {
                    this.setScreaming(false);
                }
            }

            // 首次进入世界时吼叫 - 只在非攻击状态下进行
            if (!this.hasScreamedInitially && this.tickCount > 5 && !this.isAttacking()) {
                this.startScreaming();
                this.hasScreamedInitially = true;
                this.screamCooldown = 600;
            }

            // 冷却计时器 - 只在非吼叫和非攻击状态下计时
            if (this.screamCooldown > 0 && this.screamTicks <= 0 && !this.isAttacking()) {
                this.screamCooldown--;

                // 冷却结束时再次吼叫
                if (this.screamCooldown <= 0) {
                    this.startScreaming();
                    this.screamCooldown = 600;
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "main_controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<DogJiaoJiaoJiao> state) {
        if (this.isScreaming()) {
            return state.setAndContinue(SCREAM_ANIM);
        }

        if (this.isAttacking()) {
            return state.setAndContinue(ATTACK_ANIM);
        }

        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }

        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
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
        // 确保当Boss死亡或被移除时，血条也从所有玩家屏幕上消失
        this.bossEvent.removeAllPlayers();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 目标选择器 - 提高玩家检测优先级
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this)); // 替换PersistentHurtByTargetGoal
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true)); // 提高优先级

        // 目标选择器
        this.goalSelector.addGoal(1, new DogJiaoJiaoJiaoAttackGoal(this, 1.0D, false)); // 降低攻击AI优先级
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        amount*=0.2f;

        if (source.getEntity() instanceof Player player) {
            this.setTarget(player);
            this.setLastHurtByMob(player);
            lastHatedUUID = player.getUUID();
            hateCooldown = HATE_COOLDOWN;
        }

        return super.hurt(source, amount);
    }

    private void manageHateMemory() {
        LivingEntity currentTarget = this.getTarget();

        // 如果有当前目标，记住它
        if (currentTarget != null) {
            lastHatedUUID = currentTarget.getUUID();
            hateCooldown = HATE_COOLDOWN;
        }

    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 1.0);
        builder = builder.add(Attributes.MAX_HEALTH, 600);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 32);
        builder = builder.add(Attributes.ATTACK_SPEED, 2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 128);
        return builder;
    }
}
