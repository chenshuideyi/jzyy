package com.csdy.network;

import com.csdy.ModMain;
import com.csdy.frames.diadema.DiademaSyncing;
import com.csdy.frames.diadema.packets.DiademaCreatedPacket;
import com.csdy.network.packets.AvaritaPacket;
import com.csdy.network.packets.SonicBoomPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ParticleSyncing {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ModMain.MODID, "particle"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public static void Init() {
        int packetId = 0;
        CHANNEL.registerMessage(
                packetId++,
                SonicBoomPacket.class,
                SonicBoomPacket::encode,
                SonicBoomPacket::decode,
                SonicBoomPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
                packetId++,
                AvaritaPacket.class,
                AvaritaPacket::encode,
                AvaritaPacket::decode,
                AvaritaPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }



}
