package com.csdy.jzyy.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlaySoundPacket(Vec3 origin, ResourceLocation soundEvent, float volume, float pitch) {

    public static void encode(PlaySoundPacket packet, FriendlyByteBuf buf) {
        // 写入坐标
        buf.writeDouble(packet.origin.x);
        buf.writeDouble(packet.origin.y);
        buf.writeDouble(packet.origin.z);

        // 写入声音参数
        buf.writeResourceLocation(packet.soundEvent);
        buf.writeFloat(packet.volume);
        buf.writeFloat(packet.pitch);
    }

    public static PlaySoundPacket decode(FriendlyByteBuf buf) {
        Vec3 origin = new Vec3(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
        ResourceLocation soundEvent = buf.readResourceLocation();
        float volume = buf.readFloat();
        float pitch = buf.readFloat();
        return new PlaySoundPacket(origin, soundEvent, volume, pitch);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PlaySoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            LocalPlayer player = Minecraft.getInstance().player;
            if (level == null || player == null) return;
            // 计算相对音量（基于距离衰减）
            float distance = (float) player.distanceToSqr(packet.origin);
            float attenuatedVolume = packet.volume * (1.0f - Mth.clamp(distance / 256f, 0f, 0.8f));

            level.playSound(
                    player,
                    packet.origin.x,
                    packet.origin.y,
                    packet.origin.z,
                    BuiltInRegistries.SOUND_EVENT.get(packet.soundEvent),
                    SoundSource.PLAYERS,
                    attenuatedVolume,
                    packet.pitch
            );

        });
        ctx.get().setPacketHandled(true);
    }
}
