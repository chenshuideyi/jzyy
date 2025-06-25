package com.csdy.jzyy.modifier.util.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.csdy.jzyy.modifier.util.JzyyRenderType.GOC_RENDER;
import static com.csdy.jzyy.modifier.util.JzyyRenderType.SCP_RENDER;

@OnlyIn(Dist.CLIENT)
public class GocLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation GOC = new ResourceLocation("jzyy:textures/models/goc.png");

    private final PlayerRenderer render;

    public GocLayer(PlayerRenderer renderer) {
        super(renderer);
        this.render = renderer;
    }

    public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light,
                       @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.getName().getString().equals("Dev")) return;

        if (!player.isInvisible()) {
            matrix.pushPose(); // 1. 保存初始状态

            ModelPart bodyPart = this.getParentModel().body;

            // 2. 手动应用身体部件的枢轴点平移和主要的Y轴旋转
            // 以此建立一个“直立的”但跟随身体朝向的基准坐标系。
            // bodyPart.x, y, z 是枢轴点，单位是模型单位，需要转换为世界单位。
            matrix.translate(bodyPart.x / 16.0F, bodyPart.y / 16.0F, bodyPart.z / 16.0F);

            // bodyPart.yRot 是身体的Y轴旋转（左右朝向），单位是弧度。
            if (bodyPart.yRot != 0.0F) {
                matrix.mulPose(Axis.YP.rotation(bodyPart.yRot));
            }
            // 此时，矩阵的局部坐标系：
            // +Y 轴: 向上 (与世界Y轴平行)
            // +X 轴: 指向身体的右侧 (已考虑身体yRot)
            // +Z 轴: 指向身体的前方 (已考虑身体yRot)
            // 这个坐标系不受 bodyPart.xRot (俯仰) 或 bodyPart.zRot (侧倾) 的影响。

            // --- 计算相对于这个“直立身体坐标系”的平移 ---
            float modelUnitsToWorldUnits = 1.0F / 16.0F;

            // X轴: 水平微调 (如果需要的话)
            float horizontalAdjustment_worldUnits = 0.0F; // 正值向右，负值向左

            // Y轴: 垂直居中于身体模型。
            // 这些 addBox 参数是相对于 bodyPart 枢轴点的。
            // 假设标准身体模型 addBox(?, 0.0F, ?, ?, 12.0F, ?)
            float bodyMeshOriginY_modelUnits = 0.0F;  // 身体模型视觉部分的Y起始 (模型单位)
            float bodyMeshHeight_modelUnits = 12.0F; // 身体模型视觉部分的高度 (模型单位)
            float bodyCenterY_modelUnits = bodyMeshOriginY_modelUnits + (bodyMeshHeight_modelUnits / 2.0F);
            float finalYTranslation_worldUnits = bodyCenterY_modelUnits * modelUnitsToWorldUnits;

            // Z轴: 定位到身体模型后表面，并增加额外距离。
            // 假设标准身体模型 addBox(?, ?, -2.0F, ?, ?, 4.0F)
            float bodyMeshOriginZ_modelUnits = -2.0F; // 身体模型视觉部分的Z起始 (模型单位)
            float bodyMeshDepth_modelUnits   = 4.0F;  // 身体模型视觉部分的深度 (模型单位)
            // 身体后表面的Z坐标 (模型单位, 在当前局部+Z方向上)
            float bodyBackSurfaceZ_modelUnits = bodyMeshOriginZ_modelUnits + bodyMeshDepth_modelUnits; // e.g., -2.0F + 4.0F = +2.0F
            float bodyBackSurfaceZ_worldUnits = bodyBackSurfaceZ_modelUnits * modelUnitsToWorldUnits;

            float desiredDistanceBehindSurface_worldUnits = 0.2F; // 你希望在背后飘浮多远
            float finalZTranslation_worldUnits = bodyBackSurfaceZ_worldUnits + desiredDistanceBehindSurface_worldUnits;

            // 3. 应用平移，将原点移动到物体应在的背后居中位置
            matrix.translate(
                    (double)horizontalAdjustment_worldUnits, // X轴调整
                    finalYTranslation_worldUnits,            // Y轴居中
                    finalZTranslation_worldUnits             // Z轴定位到背后
            );

            // 4. 定向物体：使其面朝外（即指向玩家的后方）
            // 当前局部+Z轴是指向身体前方的。玩家的后方是当前局部-Z轴。
            // 我们的四边形默认法线是其局部+Z。要让它指向当前变换的-Z轴，
            // 我们绕Y轴旋转180度。
            matrix.mulPose(Axis.YP.rotationDegrees(180.0F));
            // 现在，物体的局部+Z轴（即其法线）指向了玩家的后方。

            // 5. (可选) 在当前平面内旋转图案本身
            // 此时，物体的局部Z轴是其法线。绕此轴旋转即在平面内旋转。
            matrix.mulPose(Axis.ZP.rotationDegrees(35.0F)); // 例如，图案旋转35度

            // 6. 缩放
            matrix.pushPose();
            matrix.scale(6.0F, 6.0F, 6.0F); // 调整缩放大小

            // 7. 渲染
            VertexConsumer builder = renderer.getBuffer(GOC_RENDER.apply(GOC));
            Matrix4f pose = matrix.last().pose();
            float s = 0.5F; // 四边形半尺寸

            builder.vertex(pose, -s, -s, 0.0F).color(0, 255, 0, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
            builder.vertex(pose,  s, -s, 0.0F).color(0, 255, 0, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
            builder.vertex(pose,  s,  s, 0.0F).color(0, 255, 0, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
            builder.vertex(pose, -s,  s, 0.0F).color(0, 255, 0, 255).uv(0.0F, 0.0F).uv2(light).endVertex();

            matrix.popPose(); // 恢复缩放前
            matrix.popPose(); // 恢复初始
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
