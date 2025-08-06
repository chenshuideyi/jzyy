package com.csdy.jzyy.mixins;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = Integer.MAX_VALUE)
public abstract class LivingEntityMixin {

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void HorologiumNoAI(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (self.hasEffect(JzyyEffectRegister.HOROLOGIUM_NO_AI.get())) {
            ci.cancel();
        }
    }

}
