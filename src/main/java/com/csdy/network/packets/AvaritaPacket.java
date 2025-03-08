package com.csdy.network.packets;

import com.csdy.particleUtils.PointSets;
import com.csdy.sounds.SoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AvaritaPacket(Vec3 player_position) {

    public static void encode(AvaritaPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.player_position.x);
        buf.writeDouble(packet.player_position.y);
        buf.writeDouble(packet.player_position.z);

    }

    public static AvaritaPacket decode(FriendlyByteBuf buf) {
        var playerpos = new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble());
        return new AvaritaPacket(playerpos);
    }

    public static void handle(AvaritaPacket packet, Supplier<NetworkEvent.Context> network){
        network.get().enqueueWork(()->{
            Level level = Minecraft.getInstance().level;
            if (level == null) return;
            level.playSound(null, packet.player_position.z,packet.player_position.y,packet.player_position.z,SoundsRegister.MELTDOWN.get(), SoundSource.MUSIC, 1.0F, 1.0F);
            network.get().setPacketHandled(true);
        });
    }



}
