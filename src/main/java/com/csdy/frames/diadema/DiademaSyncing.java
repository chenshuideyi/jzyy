package com.csdy.frames.diadema;

import com.csdy.ModMain;
import com.csdy.frames.diadema.packets.DiademaCreatedPacket;
import com.csdy.frames.diadema.packets.DiademaRemovedPacket;
import com.csdy.frames.diadema.packets.DiademaUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DiademaSyncing {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ModMain.MODID, "diadema"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public static void Init() {
        int packetId = 0;
        CHANNEL.registerMessage(
                packetId++,
                DiademaCreatedPacket.class,
                DiademaCreatedPacket::encode,
                DiademaCreatedPacket::decode,
                DiademaSyncing::handleCreation,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
                packetId++,
                DiademaRemovedPacket.class,
                DiademaRemovedPacket::encode,
                DiademaRemovedPacket::decode,
                DiademaSyncing::handleRemoval,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
                packetId++,
                DiademaUpdatePacket.class,
                DiademaUpdatePacket::encode,
                DiademaUpdatePacket::decode,
                DiademaSyncing::handleUpdate,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }


    // sync logic
    private static final Map<Long, ClientDiadema> instances = new HashMap<>();

    public static void handleCreation(DiademaCreatedPacket packet, Supplier<NetworkEvent.Context> network) {
        network.get().enqueueWork(() -> {
            instances.replace(packet.instanceId(), packet.type().CreateClientInstance());
            System.out.println("1111111111\n1111111111\n111111111111111111111111111111111111111111111111111\n\n\n\n\n\n111");
        });
    }

    private static void handleRemoval(DiademaRemovedPacket packet, Supplier<NetworkEvent.Context> network) {
        network.get().enqueueWork(() -> {
            var instance = instances.remove(packet.instanceId());
            if (instance != null) instance.remove();
        });
    }

    private static void handleUpdate(DiademaUpdatePacket packet, Supplier<NetworkEvent.Context> network) {
        network.get().enqueueWork(() -> {
            var instance = instances.getOrDefault(packet.instanceId(), null);
            if (instance != null) instance.update(packet);
        });
    }


    // event handlers
    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut e) {
        instances.values().forEach(ClientDiadema::remove);
        instances.clear();
    }
}
