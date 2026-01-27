package com.csdy.jzyy.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

//    @Shadow
//    public static int fps;

//    @Inject(method = "runTick", at = @At("TAIL"))
//    private void onRunTick(boolean render, CallbackInfo ci) {
//        int realFps = fps;
//        fps = realFps * 5;
//    }

    //显然上面的更好玩

    // 但是显然上面的很奇异搞笑，是该做成只有csdy出场时候才生效

    @Redirect(
            method = "runTick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;fps:I", opcode = 179)
    )
    private void onSetFps(int newValue) {
        Minecraft.fps = newValue * 255;
    }

}
