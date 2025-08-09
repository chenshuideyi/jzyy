package com.csdy.jzyy.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

// 一个简单的网络包，只包含粒子生成的位置信息
public record HolyProtectionParticlePacket(Vec3 position) {

    public static void encode(HolyProtectionParticlePacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.position.x());
        buf.writeDouble(packet.position.y());
        buf.writeDouble(packet.position.z());
    }

    public static HolyProtectionParticlePacket decode(FriendlyByteBuf buf) {
        return new HolyProtectionParticlePacket(new Vec3(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        ));
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(HolyProtectionParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // **核心改动**：在客户端接收到包后，生成烟雾粒子
            for (int i = 0; i < 20; i++) {
                Minecraft.getInstance().level.addParticle(
                        ParticleTypes.POOF, // 烟雾粒子类型
                        packet.position.x(),
                        packet.position.y() + Mth.nextDouble(Minecraft.getInstance().level.random, 0.5, 1.5), // 在玩家y坐标附近生成
                        packet.position.z(),
                        0.0, 0.0, 0.0
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
