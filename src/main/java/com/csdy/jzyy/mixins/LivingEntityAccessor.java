package com.csdy.jzyy.mixins;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("DATA_HEALTH_ID")
    static EntityDataAccessor<Float> HEALTH() {
        return null;
    }

    @Accessor("lastHurt")
    void setLastHurt(float hurt);

    @Accessor("noActionTime")
    void setNoActionTime(int noActionTime);

    @Accessor("lastHurtByPlayerTime")
    void setLastHurtByPlayerTime(int noActionTime);

    @Accessor("lastHurtByPlayer")
    void setLastHurtByPlayer(Player player);

    @Accessor("lastDamageSource")
    void setLastDamageSource(DamageSource source);

    @Accessor("lastDamageStamp")
    void setLastDamageStamp(long time);

    @Accessor("dead")
    boolean dead();

    @Accessor("dead")
    void setDead(boolean b);

    @Accessor("deathScore")
    int deathScore();

    @Accessor("activeEffects")
    Map<MobEffect, MobEffectInstance> activeEffects();

}

