package com.csdy.jzyy.shader;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.network.JzyySyncing;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BatBlindnessEffect {
    private static PostChain postChain;
    private static EffectInstance effectInstance;
    private static final ResourceLocation POST_CHAIN_LOCATION = new ResourceLocation(JzyyModMain.MODID, "shaders/post/bat_blind.json");

    private static final int LOOP_TIME_MS = 10000;

    private static boolean isBat = false;

    @OnlyIn(Dist.CLIENT) @SneakyThrows
    public static void init() {
        Minecraft mc = Minecraft.getInstance();
        if (postChain != null) {
            postChain.close(); // Close existing if any
        }
        postChain = new PostChain(
                mc.getTextureManager(),
                mc.getResourceManager(),
                mc.getMainRenderTarget(), // This is the main screen framebuffer
                POST_CHAIN_LOCATION
        );
    }

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public static void onRenderArm(RenderArmEvent event) {
        if (isBat) event.setCanceled(true);
    }

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (!isBat) return; // 我感觉这样检测纯客户端可能有问题，但Csdy说没问题，那就这样

        float time = (System.currentTimeMillis() % LOOP_TIME_MS) / (float) LOOP_TIME_MS;

        Minecraft mc = Minecraft.getInstance();
        postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        if (effectInstance == null) {
            // 一些必要的初始化
            effectInstance = postChain.passes.get(0).getEffect();
        }


        var camera = event.getCamera();
        var viewMat = new Matrix4f()  // mojang 简直死妈了，一个b视图矩阵我试了几万种方法拿不到，最后还得手搓一个
                .rotateLocalY((float) Math.toRadians(camera.getYRot()))
                .rotateLocalX((float) Math.toRadians(camera.getXRot()))
                .translate(
                        (float) camera.getPosition().x,
                        (float) -camera.getPosition().y,
                        (float) camera.getPosition().z
                );
        var invProjMat = RenderSystem.getProjectionMatrix().invert();
        var invViewMat = viewMat.invert();
        effectInstance.safeGetUniform("CycleTime").set(time);
        effectInstance.safeGetUniform("InvProjMat").set(invProjMat);
        effectInstance.safeGetUniform("InvViewMat").set(invViewMat);
        postChain.process(event.getPartialTick());
    }

    @SubscribeEvent
    public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        isBat = false;
    }

    public static void SetEnableTo(ServerPlayer player, boolean enable) {
        JzyySyncing.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                enable ? BatBlindnessEffect.Packet.enable : BatBlindnessEffect.Packet.disable
        );
    }

    public static class Packet {
        private Packet(boolean enable) {
            this.isEnable = enable;
        }

        boolean isEnable;
        public static final Packet enable = new Packet(true);
        public static final Packet disable = new Packet(false);

        public static void encode(Packet packet, FriendlyByteBuf buf) {
            buf.writeBoolean(packet.isEnable);
        }

        public static Packet decode(FriendlyByteBuf buf) {
            return buf.readBoolean() ? enable : disable;
        }

        public static void handle(Packet packet, Supplier<NetworkEvent.Context> network) {
            network.get().enqueueWork(() -> {
                isBat = packet.isEnable;
            });
            network.get().setPacketHandled(true);
        }
    }
}
