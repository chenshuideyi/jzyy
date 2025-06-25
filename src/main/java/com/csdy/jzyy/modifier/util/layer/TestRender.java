package com.csdy.jzyy.modifier.util.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.OptionalDouble;
@OnlyIn(Dist.CLIENT)
public class TestRender {
//    private static final RenderType CUSTOM_LINES = RenderType.create(
//            "custom_lines",
//            DefaultVertexFormat.POSITION_COLOR_NORMAL,
//            VertexFormat.Mode.LINES,
//            256,
//            RenderType.CompositeState.builder()
//                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(256)))
//                    .createCompositeState(false)
//    );

//    private static RenderType createLineRenderType(float lineWidth) {
//        return RenderType.create(
//                "custom_line_type",
//                DefaultVertexFormat.POSITION_COLOR_NORMAL,
//                VertexFormat.Mode.LINES,
//                256,
//                false,  // 不使用delegate
//                false,  // 不需要排序
//                RenderType.CompositeState.builder()
//                        .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
//                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(lineWidth)))
//                        .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
//                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//                        .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
//                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
//                        .setCullState(RenderStateShard.NO_CULL)
//                        .createCompositeState(false)
//        );
//    }

    public static void renderCircle(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Player player,
            float radius,
            int segments,
            float r, float g, float b, float a
    ) {
        // 计算玩家脚底位置（Y坐标）
        float footY = (float) (player.getY() + 2.5);

        poseStack.pushPose();

        // 移动到玩家位置（相对于相机）
        poseStack.translate(
                player.getX() - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x,
                footY - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y,
                player.getZ() - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z
        );

        // 获取当前变换矩阵
        Matrix4f pose = poseStack.last().pose();

        // 准备线条渲染
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.END_PORTAL);
        float lineWidth = 0.05f; // 线条粗细

        // 法线向量（垂直向上）
        float nx = 0, ny = 1, nz = 0;

        // 绘制圆形
        float angleStep = (float) (2 * Math.PI / segments);
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;

            float x1 = radius * (float) Math.cos(angle1);
            float z1 = radius * (float) Math.sin(angle1);
            float x2 = radius * (float) Math.cos(angle2);
            float z2 = radius * (float) Math.sin(angle2);

            // 稍微抬高一点避免Z-fighting
            float yOffset = 0.001f;

            // 绘制线段
            consumer.vertex(pose, x1, yOffset, z1)
                    .color(r, g, b, a)
                    .normal(nx, ny, nz)
                    .endVertex();

            consumer.vertex(pose, x2, yOffset, z2)
                    .color(r, g, b, a)
                    .normal(nx, ny, nz)
                    .endVertex();
        }

        poseStack.popPose();
    }

    public static void renderSolidCircle(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Player player,
            float radius,
            int segments,
            float r, float g, float b, float a) {

        // 强制使用立即渲染模式
        if (!(bufferSource instanceof MultiBufferSource.BufferSource immediate)) {
            return;
        }

        // 确保在客户端线程
        if (!Minecraft.getInstance().isSameThread()) {
            return;
        }

        // 1. 设置渲染参数
        float yOffset = 0.15f; // 显著高于地面
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 playerPos = player.position();

        // 2. 矩阵变换
        poseStack.pushPose();
        poseStack.translate(
                playerPos.x - cameraPos.x,
                playerPos.y - cameraPos.y + yOffset,
                playerPos.z - cameraPos.z
        );

        // 3. 使用正确的RenderType
        VertexConsumer consumer = immediate.getBuffer(
                RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS)
        );

        // 4. 强制设置着色器参数
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        // 5. 构建三角形扇形
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float angleStep = (float) (2 * Math.PI / segments);

        // 中心顶点（白色）
        consumer.vertex(pose, 0, 0, 0)
                .color(1, 1, 1, a)
                .uv(0.5f, 0.5f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0xF000F0) // 全亮度
                .normal(normal, 0, 1, 0)
                .endVertex();

        // 边缘顶点（彩色）
        for (int i = 0; i <= segments; i++) {
            float angle = i * angleStep;
            float x = radius * Mth.cos(angle);
            float z = radius * Mth.sin(angle);

            consumer.vertex(pose, x, 0, z)
                    .color(r, g, b, a)
                    .uv(0.5f + 0.5f * x/radius, 0.5f + 0.5f * z/radius)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(0xF000F0)
                    .normal(normal, 0, 1, 0)
                    .endVertex();
        }

        poseStack.popPose();

        // 6. 立即刷新缓冲区
        immediate.endBatch();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    /**
     * 渲染实心圆形（末端效果专用）
     * @param poseStack 位姿堆栈（用于坐标变换）
     * @param bufferSource 缓冲区源（提供顶点数据）
     * @param player 目标玩家实体
     * @param radius 圆半径（单位：方块）
     * @param segments 分段数（越高越圆滑）
     */
    public static void renderSmoothSolidCircle( // Renamed for clarity
                                                PoseStack poseStack,
                                                MultiBufferSource bufferSource,
                                                Player player,
                                                float partialTicks, // Added partialTicks parameter
                                                float radius,
                                                int segments
    ) {
        // --- Interpolated Positioning ---
        // Interpolate the player's position between the previous tick and the current tick
        // using partialTicks for smooth rendering.
        double interpolatedPlayerX = player.xo + (player.getX() - player.xo) * partialTicks;
        double interpolatedPlayerY = player.yo + (player.getY() - player.yo) * partialTicks;
        double interpolatedPlayerZ = player.zo + (player.getZ() - player.zo) * partialTicks;

        // Use the interpolated Y coordinate for the ground level
        float groundY = (float) interpolatedPlayerY;
        // Small offset to prevent Z-fighting with the ground/blocks below
        float yOffset = 0.01f; // Might need slight adjustment

        // Get the camera's position (which is also interpolated by the game)
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();

        // Translate coordinate system to the INTERPOLATED player's ground position + offset
        // Relative to the camera's position for rendering
        poseStack.translate(
                interpolatedPlayerX - cameraPos.x(),
                (groundY + yOffset) - cameraPos.y(), // Use interpolated Y + offset
                interpolatedPlayerZ - cameraPos.z()
        );

        // Get the final transformation matrix for vertex positions
        Matrix4f pose = poseStack.last().pose();

        // --- Rendering Setup ---
        // Use RenderType.lightning() for simple, solid, colored, potentially translucent triangles
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.END_GATEWAY);

        // --- Drawing the Triangle Fan ---
        float angleStep = (float) (2 * Math.PI / segments);

        // Draw the circle using triangles radiating from the center (Triangle Fan)
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep; // Angle for the next vertex

            // Calculate vertex positions on the circumference for the current segment
            float x1 = radius * (float) Math.cos(angle1);
            float z1 = radius * (float) Math.sin(angle1);
            float x2 = radius * (float) Math.cos(angle2);
            float z2 = radius * (float) Math.sin(angle2);

            // Add vertices for the triangle: Center, Point i, Point i+1
            // The Y coordinate is 0 here because we already translated the poseStack
            // to the desired interpolated height.

            // Center vertex (0, 0, 0 in the translated space)
            consumer.vertex(pose, 0, 0, 0)
                    .endVertex();

            // Point i on circumference
            consumer.vertex(pose, x1, 0, z1)
                    .endVertex();

            // Point i+1 on circumference
            consumer.vertex(pose, x2, 0, z2)
                    .endVertex();
        }

        // Restore the previous pose state
        poseStack.popPose();
    }


//     public static void renderCircle(
//             PoseStack poseStack,
//             MultiBufferSource bufferSource,
//             Player player,
//             float radius,
//             int segments,
//             float r, float g, float b, float a
//     ) {float footY = (float) (player.getY() - player.getBbHeight() / 2.0) + 1;
//        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
//        poseStack.pushPose();
//        poseStack.translate(
//                -camera.getPosition().x,
//                -camera.getPosition().y,
//                -camera.getPosition().z
//        );
//        poseStack.translate(player.getX(), player.getY(), player.getZ());
//        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
//        Matrix4f pose = poseStack.last().pose();
//        float nx = 0, ny = 1, nz = 0;
//        poseStack.pushPose();
//        poseStack.translate(
//                -camera.getPosition().x,
//                -camera.getPosition().y,
//                -camera.getPosition().z
//        );
//        poseStack.translate(player.getX(), player.getY(), player.getZ());
//
//        float angleStep = (float) (2 * Math.PI / segments);
//        for (int i = 0; i < segments; i++) {
//            float angle1 = i * angleStep;
//            float angle2 = (i + 1) * angleStep;
//
//            float x1 = radius * (float) Math.cos(angle1);
//            float z1 = radius * (float) Math.sin(angle1);
//            float x2 = radius * (float) Math.cos(angle2);
//            float z2 = radius * (float) Math.sin(angle2);
//            float yOffset = 0.001f;
//
//            consumer.vertex(pose, x1, yOffset, z1)
//                    .color(r, g, b, a)
//                    .normal(nx, ny, nz)
//                    .endVertex();
//
//            consumer.vertex(pose, x2 , yOffset, z2)
//                    .color(r, g, b, a)
//                    .normal(nx, ny, nz)
//                    .endVertex();
//        }
//        poseStack.popPose();
//    }

}
