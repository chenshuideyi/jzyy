package com.csdy.jzyy.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class LxTrackSessionMixin {

    @Inject(method = "destroy", at = @At("HEAD"))
    private void onGameExit(CallbackInfo ci) {
        try {
            com.csdy.jzyy.JzyyConfig.LXTRACK_AND_JZYY.set(false);
        } catch (Throwable ignored) {
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onGameExit1(CallbackInfo ci) {
        try {
            com.csdy.jzyy.JzyyConfig.LXTRACK_AND_JZYY.set(false);
        } catch (Throwable ignored) {
        }
    }
}