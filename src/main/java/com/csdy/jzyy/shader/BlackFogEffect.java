package com.csdy.jzyy.shader;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.network.JzyySyncing;
import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BlackFogEffect {
    private static PostChain postChain;
    private static EffectInstance effectInstance;
    private static final ResourceLocation POST_CHAIN_LOCATION = new ResourceLocation(JzyyModMain.MODID, "shaders/post/black_fog.json");

    private static final int LOOP_TIME_MS = 10000;
    private static boolean isBlackFog = false;

    @OnlyIn(Dist.CLIENT) @SneakyThrows
    public static void init() {
        Minecraft mc = Minecraft.getInstance();
        if (postChain != null) {
            postChain.close();
        }
        postChain = new PostChain(
                mc.getTextureManager(),
                mc.getResourceManager(),
                mc.getMainRenderTarget(),
                POST_CHAIN_LOCATION
        );
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) return;
        if (!isBlackFog) return;

        Minecraft mc = Minecraft.getInstance();

        // 确保后处理链已初始化
        if (postChain == null) {
            init();
        }

        postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        if (effectInstance == null && !postChain.passes.isEmpty()) {
            effectInstance = postChain.passes.get(0).getEffect();
        }

        if (effectInstance == null) return;

        float time = (System.currentTimeMillis() % LOOP_TIME_MS) / (float) LOOP_TIME_MS;

        var camera = event.getCamera();
        var viewMat = new Matrix4f()
                .rotateLocalY((float) Math.toRadians(camera.getYRot()))
                .rotateLocalX((float) Math.toRadians(camera.getXRot()))
                .translate(
                        (float) -camera.getPosition().x,
                        (float) -camera.getPosition().y,
                        (float) -camera.getPosition().z
                );

        Matrix4f invProjMat = new Matrix4f(RenderSystem.getProjectionMatrix());
        invProjMat.invert();

        Matrix4f invViewMat = new Matrix4f(viewMat);
        invViewMat.invert();

        effectInstance.safeGetUniform("CycleTime").set(time);
        effectInstance.safeGetUniform("InvProjMat").set(invProjMat);
        effectInstance.safeGetUniform("InvViewMat").set(invViewMat);
        effectInstance.safeGetUniform("Time").set(System.currentTimeMillis() / 1000.0f);

        // 关键修改：使用正确的深度测试设置
        RenderSystem.depthMask(false);  // 禁用深度写入，避免影响场景深度
        RenderSystem.enableDepthTest(); // 启用深度测试
        RenderSystem.depthFunc(GL11.GL_LEQUAL); // 使用LEQUAL确保只影响天空（深度值最大的部分）
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        postChain.process(event.getPartialTick());

        // 恢复渲染状态
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        isBlackFog = false;
    }

    public static void SetEnableTo(ServerPlayer player, boolean enable) {
        JzyySyncing.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                enable ? BlackFogEffect.Packet.enable : BlackFogEffect.Packet.disable
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
                isBlackFog = packet.isEnable;
            });
            network.get().setPacketHandled(true);
        }
    }
}
