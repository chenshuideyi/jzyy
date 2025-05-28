package com.csdy.jzyy.entity.boss;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BossEntity extends Monster {
    public final BossEvent bossEvent;

    private transient BossMusic clientBossMusicInstance; // transient 防止序列化，客户端专用
    private boolean musicStarted = false;

    public BossEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = (ServerBossEvent)(new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS // 血条样式
        )).setDarkenScreen(true); // 是否使屏幕变暗
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pServerPlayer) {
        super.startSeenByPlayer(pServerPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pServerPlayer) {
        super.stopSeenByPlayer(pServerPlayer);
    }

    public ResourceLocation getBossBarOverlay() {
        return null;
    }

    public Font getBossBarFont() {
        return Minecraft.getInstance().font;
    }

    public SoundEvent getBossMusic() {
        return null;
    }

    public float getMaxDamageHurt() {
        return Float.MAX_VALUE;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return super.doHurtTarget(pEntity);
    }

    public BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance) {
        return false;
    }

    @Override
    public void forceAddEffect(MobEffectInstance pInstance, @Nullable Entity pEntity) {

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (isUnsafeDamage(pSource)) return false;
        this.setDeltaMovement(Vec3.ZERO);
        Entity entity = pSource.getEntity();
        if (entity instanceof LivingEntity living && !(living instanceof BossEntity) && !(living instanceof Player)) this.setTarget(living);
        if (entity instanceof ServerPlayer player && !player.isCreative()) this.setTarget(player);
        return super.hurt(pSource, pAmount);
    }

    public static boolean isUnsafeDamage(DamageSource d) {
        return d.is(DamageTypes.GENERIC)
                || d.is(DamageTypes.GENERIC_KILL)
                || d.is(DamageTypes.FELL_OUT_OF_WORLD)
                || d.is(DamageTypes.ARROW)
                || d.is(DamageTypes.WITHER)
                || d.is(DamageTypes.WITHER_SKULL)
                || d.is(DamageTypes.EXPLOSION)
                || d.is(DamageTypes.MAGIC);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth < getHealth() - getMaxDamageHurt()) pHealth = getHealth() - getMaxDamageHurt();
        super.setHealth(pHealth);
    }

    @Override
    public void kill() {
    }

    @Override
    public void tick() {
        super.tick();
        // bossEvent 更新逻辑保持不变
        this.bossEvent.setName(this.getDisplayName());
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.resetFallDistance(); // 你有两行关于fallDistance的，保留一个即可

        if (level().isClientSide()) {
            // 音乐播放逻辑
            if (this.isAlive() && Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) > 0.0F) {
                if (!musicStarted) { // 如果音乐还没开始
                    if (clientBossMusicInstance == null || clientBossMusicInstance.isStopped()) {
                        clientBossMusicInstance = new BossMusic(this); // 创建/重新创建实例
                    }
                    // 简化播放条件：如果实例不在播放列表中，就播放它
                    // SoundManager.play() 内部会处理一些重复播放同个实例的情况
                    if (!Minecraft.getInstance().getSoundManager().isActive(clientBossMusicInstance)) {
                        Minecraft.getInstance().getSoundManager().play(clientBossMusicInstance);
                    }
                    musicStarted = true;
                }
            } else { // Boss 死亡或音乐关闭
                if (musicStarted && clientBossMusicInstance != null) {
                    Minecraft.getInstance().getSoundManager().stop(clientBossMusicInstance);
                    // clientBossMusicInstance.stop(); // 也可以，效果类似，会标记为停止
                    musicStarted = false;
                    clientBossMusicInstance = null; // 可选，下次需要时重新创建
                }
            }

        }
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }
}
