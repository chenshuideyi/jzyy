package com.csdy.jzyy.mixins;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerLevelParticleSendMixin {

    @Inject(
            method = "sendParticles(Lnet/minecraft/server/level/ServerPlayer;ZDDDLnet/minecraft/network/protocol/Packet;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSendParticles(ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket, CallbackInfoReturnable<Boolean> cir) {
        // 只处理ClientboundLevelParticlesPacket
        if (pPacket instanceof ClientboundLevelParticlesPacket particlePacket) {
            // 双重保险：如果数据包 somehow 还是超过了1000，在这里再次限制
            if (particlePacket.getCount() > 1000) {
                // 创建新的限制版数据包
                ClientboundLevelParticlesPacket limitedPacket = new ClientboundLevelParticlesPacket(
                        particlePacket.getParticle(),
                        particlePacket.isOverrideLimiter(),
                        particlePacket.getX(),
                        particlePacket.getY(),
                        particlePacket.getZ(),
                        particlePacket.getXDist(),
                        particlePacket.getYDist(),
                        particlePacket.getZDist(),
                        particlePacket.getMaxSpeed(),
                        Math.min(particlePacket.getCount(), 1000)
                );

                // 使用限制版数据包替换原数据包
                cir.setReturnValue(this.sendParticles(pPlayer, pLongDistance,
                        particlePacket.getX(), particlePacket.getY(), particlePacket.getZ(),
                        limitedPacket));
                cir.cancel();
            }
        }
    }

    @Shadow
    private boolean sendParticles(ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket) {
        return false;
    }
}
