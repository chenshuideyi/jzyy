package com.csdy.jzyy;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.csdy.jzyy.modifier.util.layer.TestRender.renderSmoothSolidCircle;


///就是这里，做个标记点
//@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        // 只在实体渲染后阶段执行
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        // 获取PoseStack和BufferSource
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // 保存当前变换状态
        poseStack.pushPose();

        try {
            // 将坐标系移动到玩家位置
            Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
            poseStack.translate(
                    player.getX() - cameraPos.x,
                    player.getY() - cameraPos.y,
                    player.getZ() - cameraPos.z
            );
            float partialTicks = event.getPartialTick(); // Get partial ticks from event
//            long time = System.currentTimeMillis();
//            float hue = (time % 5000)/2000f;
//            int rgb = Color.HSBtoRGB(hue, 0.8f, 1.0f);
//            float r = ((rgb >> 16) & 0xFF) / 255f;
//            float g = ((rgb >> 8) & 0xFF) / 255f;
//            float b = (rgb & 0xFF) / 255f;
            // 渲染圆形光环
//            renderCircle(
//                    poseStack,
//                    bufferSource,
//                    player,
//                    4.0f,  // 半径
//                    32,   // 分段数
//                    r, g, b, 0.5f  // RGBA颜色(红色)
//            );
            renderSmoothSolidCircle(
                    poseStack,
                    bufferSource,
                    player,
                    partialTicks,
                    6.0f,  // 半径
                    12  // 分段数
//                    r, g, b, 0.5f  // RGBA颜色(红色)
            );
        } finally {
            // 恢复变换状态
            poseStack.popPose();
            // 如果使用BufferSource需要手动结束
            bufferSource.endBatch();
        }
    }
}
