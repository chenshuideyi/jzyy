package com.csdy.jzyy.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "releaseMouse", at = @At("HEAD"), cancellable = true)
    private void releaseMouse(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof DeathScreen) {
            ci.cancel();
        }
    }
}