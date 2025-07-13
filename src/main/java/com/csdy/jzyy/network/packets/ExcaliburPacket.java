package com.csdy.jzyy.network.packets;

import com.csdy.jzyy.particle.register.JzyyParticlesRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ExcaliburPacket(Vec3 origin) {
    public static void encode(ExcaliburPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.origin.x);
        buf.writeDouble(packet.origin.y);
        buf.writeDouble(packet.origin.z);
    }

    public static ExcaliburPacket decode(FriendlyByteBuf buf) {
        Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new ExcaliburPacket(origin);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(ExcaliburPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            @NotNull SimpleParticleType types = JzyyParticlesRegister.SABER_PARTICLE.get();

            RandomSource random = RandomSource.create();
            for (int i = 0; i < 60; i++) {
                float radius = 1.5f + random.nextFloat() * 5f;
                float angle = random.nextFloat() * Mth.TWO_PI;
                float x = Mth.cos(angle) * radius;
                float z = Mth.sin(angle) * radius;

                float speedX = (random.nextFloat() - 0.5f) * 0.02f;
                float speedY = 0.1f + random.nextFloat() * 0.3f;    // 缓慢上升
                float speedZ = (random.nextFloat() - 0.5f) * 0.02f;

                level.addParticle(
                        types,
                        packet.origin.x + x,
                        packet.origin.y - 0.5,
                        packet.origin.z + z,
                        speedX,
                        speedY,
                        speedZ
                );

            }
        });
        ctx.get().setPacketHandled(true);
    }
}
