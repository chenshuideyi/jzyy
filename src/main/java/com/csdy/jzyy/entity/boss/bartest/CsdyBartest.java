package com.csdy.jzyy.entity.boss.bartest;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CsdyBartest {
    static Minecraft mc = Minecraft.getInstance();
    static Window window = mc.getWindow();

    // 用于计算彩虹色的时间变量
    private static long startTime = System.currentTimeMillis();

    public static int rgba(int red, int green, int blue, int alpha) {
        alpha %= 256;
        red %= 256;
        green %= 256;
        blue %= 256;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // 获取彩虹色
    public static int getRainbowColor(float offset, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() - startTime + (long)(offset * 1000)) % 5000) / 5000.0f;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    // 获取渐变彩虹色条
    public static void fillRainbow(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        for (int i = 0; i < width; i++) {
            float progress = (float)i / width;
            int color = getRainbowColor(progress * 2.0f, 0.8f, 0.8f);
            guiGraphics.fill(x + i, y, x + i + 1, y + height, color | 0xFF000000);
        }
    }

    // 绘制不规则边框
    public static void drawIrregularBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int borderColor = rgba(180, 180, 180, 200);
        int shadowColor = rgba(50, 50, 50, 150);

        // 波浪参数
        float waveFrequency = 0.1f;
        float waveAmplitude = 2.5f;
        long timeOffset = System.currentTimeMillis() / 50;

        // 顶部波浪边框
        for (int i = 0; i < width; i++) {
            float wave = (float)Math.sin(i * waveFrequency + timeOffset) * waveAmplitude;
            guiGraphics.fill(x + i, y - 1 + (int)wave, x + i + 1, y + (int)wave, borderColor);
            guiGraphics.fill(x + i, y - 2 + (int)wave, x + i + 1, y - 1 + (int)wave, shadowColor);
        }

        // 底部波浪边框
        for (int i = 0; i < width; i++) {
            float wave = (float)Math.sin(i * waveFrequency + timeOffset + 3.14f) * waveAmplitude;
            guiGraphics.fill(x + i, y + height + (int)wave, x + i + 1, y + height + 1 + (int)wave, borderColor);
            guiGraphics.fill(x + i, y + height + 1 + (int)wave, x + i + 1, y + height + 2 + (int)wave, shadowColor);
        }

        // 两侧装饰性尖刺
        int spikeCount = 5;
        for (int i = 0; i < spikeCount; i++) {
            float pos = (float)i / (spikeCount - 1);
            int spikeX = x + (int)(width * pos);
            int spikeHeight = 3 + (int)(Math.sin(pos * Math.PI * 2 + timeOffset * 0.02) * 2);

            // 左侧尖刺
            guiGraphics.fill(spikeX - 1, y - spikeHeight, spikeX, y, borderColor);
            // 右侧尖刺
            guiGraphics.fill(spikeX - 1, y + height, spikeX, y + height + spikeHeight, borderColor);
        }

        // 添加X形斜边装饰
        int xThickness = 2; // X形斜边的粗细
        int xLength = 15;   // X形斜边的长度

        // 左上到右下的斜边
        for (int i = 0; i < xLength; i++) {
            float progress = (float)i / xLength;
            int x1 = x + (int)(progress * xLength);
            int y1 = y - (int)(progress * xLength/2);
            int x2 = x1 + xThickness;
            int y2 = y1 + xThickness;
            guiGraphics.fill(x1, y1, x2, y2, borderColor);
        }

        // 右上到左下的斜边
        for (int i = 0; i < xLength; i++) {
            float progress = (float)i / xLength;
            int x1 = x + width - (int)(progress * xLength);
            int y1 = y - (int)(progress * xLength/2);
            int x2 = x1 + xThickness;
            int y2 = y1 + xThickness;
            guiGraphics.fill(x1, y1, x2, y2, borderColor);
        }

        // 左下到右上的斜边
        for (int i = 0; i < xLength; i++) {
            float progress = (float)i / xLength;
            int x1 = x + (int)(progress * xLength);
            int y1 = y + height + (int)(progress * xLength/2);
            int x2 = x1 + xThickness;
            int y2 = y1 + xThickness;
            guiGraphics.fill(x1, y1, x2, y2, borderColor);
        }

        // 右下到左上的斜边
        for (int i = 0; i < xLength; i++) {
            float progress = (float)i / xLength;
            int x1 = x + width - (int)(progress * xLength);
            int y1 = y + height + (int)(progress * xLength/2);
            int x2 = x1 + xThickness;
            int y2 = y1 + xThickness;
            guiGraphics.fill(x1, y1, x2, y2, borderColor);
        }
    }

    @SubscribeEvent
    public static void bartest(CustomizeGuiOverlayEvent.BossEventProgress event) {
        LerpingBossEvent bossStatusInfo = event.getBossEvent();
        GLFW.glfwSetWindowOpacity(window.getWindow(), 1.0f);
        if (event.getBossEvent().getName().getString().startsWith(Component.translatable("entity.jzyy.sword_man_csdy").getString())) {
            event.setCanceled(true);

            if (mc.level != null) {
                RenderSystem.enableBlend();
                GuiGraphics guiGraphics = event.getGuiGraphics();

                // 血条尺寸
                int width = 256;
                int height = 8; // 增加高度以适应波浪效果
                int x = window.getGuiScaledWidth() / 2 - width / 2;
                int y = event.getY() + 5; // 调整位置

                // 绘制不规则边框
                drawIrregularBorder(guiGraphics, x, y, width, height);

                // 背景 (半透明)
                guiGraphics.fill(x, y, x + width, y + height, rgba(30, 30, 30, 180));

                // 血条进度
                float healthPercentage = bossStatusInfo.getProgress();
                int progressWidth = (int) (width * healthPercentage);

                // 使用彩虹色填充血条
                if (progressWidth > 0) {
                    fillRainbow(guiGraphics, x, y, progressWidth, height);

                }

                // 绘制Boss名称
                String bossName = bossStatusInfo.getName().getString();
                Font font = mc.font;
                int nameColor = getRainbowColor(0, 0.7f, 1.0f);

                // 名称背景效果
                int nameWidth = font.width(bossName);
                int nameX = x + (width / 2) - (nameWidth / 2);
                int nameY = y - 12;

//                if (nameY < 5) { // 如果太靠近屏幕顶部
//                    nameY = y + height + 5; // 改为显示在血条下方
//                }

                // 名称装饰性背景
                guiGraphics.fill(nameX - 2, nameY - 2, nameX + nameWidth + 2, nameY + 10, rgba(0, 0, 0, 150));
                guiGraphics.fill(nameX - 1, nameY - 1, nameX + nameWidth + 1, nameY + 9, rgba(20, 20, 20, 180));

                // 绘制名称
                guiGraphics.drawString(font, bossName, nameX, nameY, nameColor | 0xFF000000, true);

                RenderSystem.disableBlend();
            }
        }
    }
}
