package com.csdyms.mixins;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {
    @Inject(
            method = "knockback",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onKnockback(double p_147241_, double p_147242_, double p_147243_, CallbackInfo ci) {
        if ((Object) this instanceof net.minecraft.world.entity.player.Player) {
            ci.cancel(); // 直接取消操作
        }
    }

    @Inject(
            method = "getHurtSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getHurtSound(DamageSource p_21239_, CallbackInfoReturnable<SoundEvent> ci) {
        if ((Object) this instanceof net.minecraft.world.entity.player.Player) {
            ci.cancel(); // 直接取消操作
        }
    }



    @Inject(
            method = "playHurtSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void HurtSound(DamageSource p_21160_, CallbackInfo ci) {
        if ((Object) this instanceof net.minecraft.world.entity.player.Player) {
            ci.cancel(); // 直接取消操作
        }
    }
}
