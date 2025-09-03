package com.csdy.jzyy.mixins;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.hasClearBody;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.hasImagineBreakArmor;

@Mixin(value = LivingEntity.class, priority = Integer.MAX_VALUE)
public abstract class LivingEntityMixin {

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void HorologiumNoAI(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (self.hasEffect(JzyyEffectRegister.HOROLOGIUM_NO_AI.get())) {
            ci.cancel();
        }
    }

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void onSetHealth(float health, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (!(entity instanceof Player player) || !player.isAddedToWorld()) {
            return;
        }

        if (player.isDeadOrDying()) {
            return;
        }

        if (hasImagineBreakArmor(player)) {
            float damageAmount = player.getHealth() - health;
            if (damageAmount > 7.0f) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void onAddEffect(MobEffectInstance pEffectInstance, Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (!(entity instanceof Player player) || !player.isAddedToWorld()) {
            return;
        }

        if (player.isDeadOrDying()) {
            return;
        }

        if (hasClearBody(player)) {
            if (!pEffectInstance.getEffect().isBeneficial()) {
                cir.cancel();
            }
        }
    }




}
