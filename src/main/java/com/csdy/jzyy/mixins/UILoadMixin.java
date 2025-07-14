package com.csdy.jzyy.mixins;

import com.csdy.jzyy.cheat.KeyPassNetworkValidation;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class UILoadMixin {

    @Inject(method = {"onGameLoadFinished"}, at = {@At("HEAD")})
    private void open(CallbackInfo ci) {
        KeyPassNetworkValidation.main(new String[]{});
    }
}