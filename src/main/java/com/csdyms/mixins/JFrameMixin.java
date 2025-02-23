package com.csdyms.mixins;

import com.csdy.until.CsdyJFrame;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


//@Mixin({Minecraft.class})
//public abstract class JFrameMixin {
//
//    @Shadow
//    public abstract void run();
//
//    @Inject(method = {"onGameLoadFinished"}, at = {@At("HEAD")})
//    private void open(CallbackInfo ci) {
//        CsdyJFrame.run();
//    }
//}
