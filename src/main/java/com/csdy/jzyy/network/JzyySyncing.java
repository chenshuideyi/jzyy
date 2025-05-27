package com.csdy.jzyy.network;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.network.packets.ExcaliburPacket;
import com.csdy.jzyy.network.packets.JzyySonicBoomPacket;
import com.csdy.jzyy.network.packets.UnsafeMemoryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JzyySyncing {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(JzyyModMain.MODID, "jzyy"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public static void Init() {
        int packetId = 0;
        CHANNEL.registerMessage(
                packetId++,
                UnsafeMemoryPacket.class,
                UnsafeMemoryPacket::encode,
                UnsafeMemoryPacket::decode,
                UnsafeMemoryPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT) // 明确指定方向
        );
        CHANNEL.registerMessage(
                packetId++,
                JzyySonicBoomPacket.class,
                JzyySonicBoomPacket::encode,
                JzyySonicBoomPacket::decode,
                JzyySonicBoomPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT) // 明确指定方向
        );
        CHANNEL.registerMessage(
                packetId++,
                ExcaliburPacket.class,
                ExcaliburPacket::encode,
                ExcaliburPacket::decode,
                ExcaliburPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT) // 明确指定方向
        );
    }
}
