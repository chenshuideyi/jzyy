package com.csdy.jzyy.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.csdy.jzyy.ms.CoreMsUtil.isCsdy;

@Mixin(LivingEntity.class)
public abstract class PreventDisarmamentMixin {

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    protected void preventDisarm(CallbackInfo ci) {
        if ((Object)this instanceof Player player) {
            if (isCsdy(player)) {
                ci.cancel(); // 取消原版缴械逻辑
            }
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
    private void preventDeathDrop(DamageSource source, CallbackInfo ci) {
        if ((Object)this instanceof Player player) {
            if (isCsdy(player)) {
                ci.cancel(); // 取消原版缴械逻辑
            }
        }
    }

}
