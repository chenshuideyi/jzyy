package com.csdy.jzyy.modifier.util.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.csdy.jzyy.modifier.util.JzyyRenderType.SCP_RENDER;

public class GocLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation GOC = new ResourceLocation("jzyy:textures/models/goc.png");

    private final PlayerRenderer render;

    public GocLayer(PlayerRenderer renderer) {
        super(renderer);
        this.render = renderer;
    }

    public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.isInvisible()) {
            matrix.pushPose();
            ((PlayerModel<?>)this.render.getModel()).jacket.translateAndRotate(matrix);
            double yShift = -0.498;
            if (player.isCrouching()) {
                matrix.mulPose(Axis.XP.rotationDegrees(-28.64789F));
                yShift = -0.44;
            }

            matrix.mulPose(Axis.ZP.rotationDegrees(180.0F));
            matrix.scale(3.0F, 3.0F, 3.0F);
            matrix.translate(-0.5, yShift, -0.5);
            VertexConsumer builder = renderer.getBuffer((RenderType) SCP_RENDER.apply(GOC));
            Matrix4f matrix4f = matrix.last().pose();
            builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(0, 255, 0, 255).uv(0.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, 0.0F, 0.0F, 1.0F).color(0, 255, 0, 255).uv(0.0F, 1.0F).endVertex();
            builder.vertex(matrix4f, 1.0F, 0.0F, 1.0F).color(0, 255, 0, 255).uv(1.0F, 1.0F).endVertex();
            builder.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(0, 255, 0, 255).uv(1.0F, 0.0F).endVertex();
            matrix.popPose();


        }
    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return; // 确保在实体渲染阶段之后执行
        }

        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher renderDispatcher = minecraft.getEntityRenderDispatcher();

        // 获取 PlayerRenderer
        if (minecraft.player != null && renderDispatcher.getRenderer(minecraft.player) instanceof PlayerRenderer playerRenderer) {
            playerRenderer.addLayer(new ScpLayer(playerRenderer));
        }
    }
}
