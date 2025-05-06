package com.csdy.jzyy.network.packets;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record JzyySonicBoomPacket(Vec3 origin, Vec3 direction, float power) {
    public static void encode(JzyySonicBoomPacket packet, FriendlyByteBuf buf) {
        // 写入起点位置
        buf.writeDouble(packet.origin.x);
        buf.writeDouble(packet.origin.y);
        buf.writeDouble(packet.origin.z);

        // 写入方向向量
        buf.writeDouble(packet.direction.x);
        buf.writeDouble(packet.direction.y);
        buf.writeDouble(packet.direction.z);

        // 写入威力参数
        buf.writeFloat(packet.power);
    }

    public static JzyySonicBoomPacket decode(FriendlyByteBuf buf) {
        Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        float power = buf.readFloat();
        return new JzyySonicBoomPacket(origin, direction, power);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(JzyySonicBoomPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            // 更平滑的粒子效果
            int particleCount = (int)(20 * packet.power);
            for (int i = 0; i <= particleCount; i++) {
                float progress = i / (float)particleCount;
                Vec3 particlePos = packet.origin.add(
                        packet.direction.scale(15 * packet.power * progress)
                );

                // 添加冲击波粒子
                level.addParticle(ParticleTypes.SONIC_BOOM,
                        particlePos.x, particlePos.y, particlePos.z,
                        0, 0.1, 0);

                // 添加额外效果粒子
                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.x, particlePos.y + 0.5, particlePos.z,
                        0, 0, 0);

                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.x, particlePos.y - 0.5, particlePos.z,
                        0, 0, 0);

                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.x + 0.5, particlePos.y, particlePos.z,
                        0, 0, 0);

                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.x - 0.5, particlePos.y, particlePos.z,
                        0, 0, 0);

                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.x, particlePos.y, particlePos.z  + 0.5,
                        0, 0, 0);

                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        particlePos.x, particlePos.y, particlePos.z  - 0.5,
                        0, 0, 0);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
