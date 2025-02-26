package com.csdy.frames.diadema.packets;

import net.minecraft.network.FriendlyByteBuf;

public record DiademaRemovedPacket(long instanceId) {
    public static void encode(DiademaRemovedPacket packet, FriendlyByteBuf buf) {
        buf.writeLong(packet.instanceId);
    }

    public static DiademaRemovedPacket decode(FriendlyByteBuf buf) {
        var instanceId = buf.readLong();
        return new DiademaRemovedPacket(instanceId);
    }
}
