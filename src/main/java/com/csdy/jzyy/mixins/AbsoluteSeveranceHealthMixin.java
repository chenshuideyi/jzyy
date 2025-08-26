package com.csdy.jzyy.mixins;

import mods.flammpfeil.slashblade.util.AttackHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.hasImagineBreakArmor;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.DATA_HEALTH_ID;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.getAbsoluteSeveranceHealth;

@Mixin({LivingEntity.class})
public class AbsoluteSeveranceHealthMixin {

    private boolean inGetHealth = false;

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void onGetHealth(CallbackInfoReturnable<Float> cir) {
        if (inGetHealth) {
            return;
        }

        inGetHealth = true;
        try {
            LivingEntity entity = (LivingEntity)(Object)this;
            float destructionHealth = getAbsoluteSeveranceHealth(entity);
            if (entity.getHealth() > destructionHealth) {
                cir.setReturnValue(destructionHealth);
                entity.getEntityData().set(DATA_HEALTH_ID, destructionHealth);
                cir.cancel();
            }
        } finally {
            inGetHealth = false;
        }
    }

}
