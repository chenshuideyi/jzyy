package com.csdy.network.packets;


import com.csdy.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SonicBoomPacket(Vec3 player_position, Vec3 targets_position) {

        public static void encode(SonicBoomPacket packet, FriendlyByteBuf buf) {
           buf.writeDouble(packet.player_position.x);
            buf.writeDouble(packet.player_position.y);
            buf.writeDouble(packet.player_position.z);

            buf.writeDouble(packet.targets_position.x);
            buf.writeDouble(packet.targets_position.y);
            buf.writeDouble(packet.targets_position.z);
        }

        public static SonicBoomPacket decode(FriendlyByteBuf buf) {
            var playerpos = new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble());
            var targetpos = new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble());
            return new SonicBoomPacket(playerpos, targetpos);
        }

        public static void handle(SonicBoomPacket packet, Supplier<NetworkEvent.Context> network){
            network.get().enqueueWork(()->{
                Level level = Minecraft.getInstance().level;
                if (level == null) return;
                PointSets.Line(packet.player_position, packet.targets_position, 32).forEach(v -> {
                    level.addParticle(ParticleTypes.SONIC_BOOM, v.x, v.y+1.5, v.z,0,0,0);

                });
            network.get().setPacketHandled(true);
            });
        }



}
