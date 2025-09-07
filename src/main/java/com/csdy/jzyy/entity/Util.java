package com.csdy.jzyy.entity;

import com.csdy.jzyy.ms.PLZBase;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;

public class Util {

    public static void forceHurt(LivingEntity living, DamageSource p_21016_, float p_21017_) {
        if (living.isSleeping() && !living.level().isClientSide) {
            living.stopSleeping();
        }
        living.noActionTime = 0;
        float f = p_21017_;
        boolean flag = false;
        float f1 = 0.0F;
        Entity entity1;
        LivingEntity livingentity1;
        living.walkAnimation.setSpeed(0.0F);
        living.lastHurt = p_21017_;
        living.invulnerableTime = 0;
        actuallyHurt(living, p_21016_, p_21017_);
        living.hurtDuration = 10;
        living.hurtTime = living.hurtDuration;
        entity1 = p_21016_.getEntity();
        if (entity1 != null) {
            if (entity1 instanceof LivingEntity) {
                livingentity1 = (LivingEntity) entity1;
                if (!p_21016_.is(DamageTypeTags.NO_ANGER)) {
                    living.setLastHurtByMob(livingentity1);
                }
            }
            if (entity1 instanceof Player) {
                Player player1 = (Player) entity1;
                living.lastHurtByPlayerTime = 100;
                living.lastHurtByPlayer = player1;
            } else if (entity1 instanceof TamableAnimal) {
                TamableAnimal tamableEntity = (TamableAnimal) entity1;
                if (tamableEntity.isTame()) {
                    living.lastHurtByPlayerTime = 100;
                    LivingEntity livingentity2 = tamableEntity.getOwner();
                    if (livingentity2 instanceof Player) {
                        Player player = (Player) livingentity2;
                        living.lastHurtByPlayer = player;
                    } else {
                        living.lastHurtByPlayer = null;
                    }
                }
            }
        }
        if (flag) {
            living.level().broadcastEntityEvent(living, (byte) 29);
        } else {
            living.level().broadcastDamageEvent(living, p_21016_);
        }
        living.hurtMarked = true;
        boolean flag2 = !flag || p_21017_ > 0.0F;
        if (flag2) {
            living.lastDamageSource = p_21016_;
            living.lastDamageStamp = living.level().getGameTime();
        }
        if (living instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer) living, p_21016_, f, p_21017_, flag);
            if (f1 > 0.0F && f1 < 3.4028235E37F) {
                ((ServerPlayer) living).awardStat(Stats.CUSTOM.get(Stats.DAMAGE_BLOCKED_BY_SHIELD), Math.round(f1 * 10.0F));
            }
        }
        if (entity1 instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) entity1, living, p_21016_, f, p_21017_, flag);
        }
    }

    public static void actuallyHurt(LivingEntity living, DamageSource p_21240_, float p_21241_) {
        float f1 = Math.max(p_21241_ - living.getAbsorptionAmount(), 0.0F);
        living.setAbsorptionAmount(living.getAbsorptionAmount() - (p_21241_ - f1));
        float f = p_21241_ - f1;
        if (f > 0.0F && f < 3.4028235E37F) {
            Entity entity = p_21240_.getEntity();
            if (entity instanceof ServerPlayer) {
                ServerPlayer serverplayer = (ServerPlayer) entity;
                serverplayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
            }
        }
        living.getCombatTracker().recordDamage(p_21240_, f1);
        living.setHealth(living.getHealth() - f1);
        living.setAbsorptionAmount(living.getAbsorptionAmount() - f1);
        living.gameEvent(GameEvent.ENTITY_DAMAGE);
    }
}
