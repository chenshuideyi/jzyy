package com.csdy.jzyy.mixins;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Inject(method = "releaseAll", at = @At("HEAD"), cancellable = true)
    private static void onReleaseAll(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof DeathScreen) {
            ci.cancel();
        }
    }
}
