package com.csdy.jzyy.mixins;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Inject(method = "clearContent", at = @At("HEAD"), cancellable = true)
    private void modernClearProtection(CallbackInfo ci) {
        ci.cancel();
    }

}
