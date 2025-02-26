package com.csdy.frames.diadema.packets;

import com.csdy.frames.CsdyRegistries;
import com.csdy.frames.diadema.DiademaType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public record DiademaCreatedPacket(DiademaType type, long instanceId) {
    public static void encode(DiademaCreatedPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(Objects.requireNonNull(CsdyRegistries.DIADEMA_TYPES_REG.get().getKey(packet.type)));
        buf.writeLong(packet.instanceId);
    }

    public static DiademaCreatedPacket decode(FriendlyByteBuf buf) {
        var type = CsdyRegistries.DIADEMA_TYPES_REG.get().getValue(buf.readResourceLocation());
        var instanceId = buf.readLong();
        return new DiademaCreatedPacket(type, instanceId);
    }
}
