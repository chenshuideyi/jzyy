package com.csdy.jzyy.mixins;

import mods.flammpfeil.slashblade.util.AttackHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({AttackHelper.class})
public class AttackHelperMixin {

    @ModifyArg(
            method = "handlePostAttackEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            ),
            index = 4
    )
    private static int limitDamageParticles(int originalCount) {
        return Math.min(originalCount, 1);
    }


}
