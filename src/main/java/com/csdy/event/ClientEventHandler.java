package com.csdy.event;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//public class ClientEventHandler {
//    private static final Map<Entity, Long> highlightedEntities = new ConcurrentHashMap<>();
//    private static final double RANGE = 10.0;
//    private static final int DURATION = 5000; // 高亮持续时间（毫秒）
//
//    @SubscribeEvent
//    public static void onPlaySound(PlaySoundSourceEvent event) {
//        // 只处理客户端
//        event.getSound();
//
//        Minecraft mc = Minecraft.getInstance();
//        Player player = mc.player;
//
//        // 获取声音的位置
//        double x = event.getSound().getX();
//        double y = event.getSound().getX();
//        double z = event.getSound().getX();
//        // 获取玩家附近的所有实体
//        Level level = player.level();
//        AABB area = new AABB(
//                x - RANGE, y - RANGE, z - RANGE,
//                x + RANGE, y + RANGE, z + RANGE
//        );
//
//        // 遍历范围内的实体
//        for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
//            if (entity instanceof LivingEntity) {
//                highlightedEntities.put(entity, System.currentTimeMillis());
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public static void onRenderWorldLast(RenderLevelStageEvent event) {
//        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
//
//        Minecraft mc = Minecraft.getInstance();
//        Player player = mc.player;
//        highlightedEntities.entrySet().removeIf(entry ->
//                System.currentTimeMillis() - entry.getValue() > DURATION
//        );
//
//        // 准备渲染参数
//        RenderSystem.deleteTexture(1);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.lineWidth(2.0F);
//
//        // 获取现代渲染缓冲系统
//        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//
//        // 获取指定渲染类型的顶点构建器（替代旧版BufferBuilder）
//        BufferBuilder vertexBuilder = (BufferBuilder) bufferSource.getBuffer(RenderType.lines());
//
//
//        // 遍历所有需要高亮的实体
//        highlightedEntities.keySet().forEach(entity -> {
//            if (entity.isAlive()) {
//                // 计算边界框
//                AABB aabb = entity.getBoundingBox().inflate(0.1); // 略微放大
//                Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
//
//                // 转换坐标到摄像机视角
//                double x = aabb.minX - view.x;
//                double y = aabb.minY - view.y;
//                double z = aabb.minZ - view.z;
//                double sizeX = aabb.maxX - aabb.minX;
//                double sizeY = aabb.maxY - aabb.minY;
//                double sizeZ = aabb.maxZ - aabb.minZ;
//
//                // 开始绘制红色线框
//                RenderSystem.clearColor(1.0F, 0.0F, 0.0F, 0.8F);
//                vertexBuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION);
//
//                // 绘制立方体线框
//                buildWireframe(vertexBuilder, x, y, z, sizeX, sizeY, sizeZ);
//
//                bufferSource.endBatch();
//            }
//        });
//
//        RenderSystem.depthMask(true);
//        RenderSystem.disableBlend();
//    }
//
//    private static void buildWireframe(BufferBuilder buffer, double x, double y, double z,
//                                       double sizeX, double sizeY, double sizeZ) {
//        // 底部
//        buffer.vertex(x, y, z).endVertex();
//        buffer.vertex(x + sizeX, y, z).endVertex();
//        buffer.vertex(x + sizeX, y, z + sizeZ).endVertex();
//        buffer.vertex(x, y, z + sizeZ).endVertex();
//        buffer.vertex(x, y, z).endVertex();
//
//        // 顶部
//        buffer.vertex(x, y + sizeY, z).endVertex();
//        buffer.vertex(x + sizeX, y + sizeY, z).endVertex();
//        buffer.vertex(x + sizeX, y + sizeY, z + sizeZ).endVertex();
//        buffer.vertex(x, y + sizeY, z + sizeZ).endVertex();
//        buffer.vertex(x, y + sizeY, z).endVertex();
//
//        // 垂直边
//        buffer.vertex(x, y, z).endVertex();
//        buffer.vertex(x, y + sizeY, z).endVertex();
//
//        buffer.vertex(x + sizeX, y, z).endVertex();
//        buffer.vertex(x + sizeX, y + sizeY, z).endVertex();
//
//        buffer.vertex(x + sizeX, y, z + sizeZ).endVertex();
//        buffer.vertex(x + sizeX, y + sizeY, z + sizeZ).endVertex();
//
//        buffer.vertex(x, y, z + sizeZ).endVertex();
//        buffer.vertex(x, y + sizeY, z + sizeZ).endVertex();
//    }
//}


