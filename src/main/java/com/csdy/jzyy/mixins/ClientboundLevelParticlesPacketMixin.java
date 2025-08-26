package com.csdy.jzyy.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundLevelParticlesPacket.class)
public class ClientboundLevelParticlesPacketMixin {

    @Inject(
            method = "<init>(Lnet/minecraft/core/particles/ParticleOptions;ZDDDFFFFI)V",
            at = @At("RETURN")
    )
    private void onConstructed(ParticleOptions pParticle, boolean pOverrideLimiter,
                               double pX, double pY, double pZ,
                               float pXDist, float pYDist, float pZDist,
                               float pMaxSpeed, int pCount, CallbackInfo ci) {

        ClientboundLevelParticlesPacket self = (ClientboundLevelParticlesPacket)(Object)this;
//        if (self.getCount() <= 1000 && pCount > 1000) {
//             Minecraft.LOGGER.info("Jzyy成功截断粒子数量: {} -> {}", pCount, self.getCount());
//        }
    }
}
