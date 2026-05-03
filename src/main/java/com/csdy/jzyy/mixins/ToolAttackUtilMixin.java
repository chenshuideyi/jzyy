package com.csdy.jzyy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

@Mixin(ToolAttackUtil.class)
public class ToolAttackUtilMixin {
    @ModifyArg(
            method = "performAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            ),
            index = 4
    )
    private static int limitDamageParticles(int originalCount) {
        return Math.min(originalCount, 1);
    }


    @ModifyArg(
            method = "spawnAttackParticle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            ),
            index = 4
    )
    private static int limitDamageParticles2(int originalCount) {
        return Math.min(originalCount, 1);
    }

}
