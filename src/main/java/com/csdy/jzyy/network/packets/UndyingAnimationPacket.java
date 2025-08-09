package com.csdy.jzyy.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UndyingAnimationPacket(ItemStack itemStack) {

    public static void encode(UndyingAnimationPacket packet, FriendlyByteBuf buf) {
        // 写入物品栈数据
        buf.writeItem(packet.itemStack);
    }

    public static UndyingAnimationPacket decode(FriendlyByteBuf buf) {
        // 读取物品栈数据
        ItemStack itemStack = buf.readItem();
        return new UndyingAnimationPacket(itemStack);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(UndyingAnimationPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            LocalPlayer player = Minecraft.getInstance().player;
            if (level == null || player == null) {
                return;
            }

            // **核心改动**: 收到网络包后，直接播放物品激活动画
            Minecraft.getInstance().gameRenderer.displayItemActivation(packet.itemStack());
        });
        ctx.get().setPacketHandled(true);
    }
}
