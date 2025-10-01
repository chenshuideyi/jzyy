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
        float hue = ((System.currentTimeMillis() - startTime + (long)(offset * 3000)) % 2000 / 2000.0f);
        // 添加随机亮度波动（0.8~1.0之间）
        float flicker = 0.8f + (float)Math.sin(System.currentTimeMillis() / 200.0 + offset) * 0.2f;
        return Color.HSBtoRGB(hue, saturation, brightness * flicker);
    }

    // 获取渐变彩虹色条
    public static void fillRainbow(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int segmentSize = 4; // 每10像素为一个颜色段
        for (int i = 0; i < width; i++) {
            float progress = (float)(i / segmentSize) / ((float) width / segmentSize);
            int color = getRainbowColor(progress * 3.0f, 1.0f, 1.0f);
            guiGraphics.fill(x + i, y, x + i + 1, y + height, color | 0xFF000000);
        }
    }

    public static void drawIrregularBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int borderColor = rgba(180, 180, 180, 200);
        int shadowColor = rgba(50, 50, 50, 150);

        // 主边框（1像素宽）
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y, borderColor);         // 上边框
        guiGraphics.fill(x - 1, y + height, x + width + 1, y + height + 1, borderColor); // 下边框
        guiGraphics.fill(x - 1, y, x, y + height, borderColor);                // 左边框
        guiGraphics.fill(x + width, y, x + width + 1, y + height, borderColor);// 右边框

        // 阴影效果（外扩1像素）
        guiGraphics.fill(x - 2, y - 2, x + width + 2, y - 1, shadowColor);    // 顶部阴影
        guiGraphics.fill(x - 2, y + height + 1, x + width + 2, y + height + 2, shadowColor); // 底部阴影
        guiGraphics.fill(x - 2, y - 1, x - 1, y + height + 1, shadowColor);   // 左侧阴影
        guiGraphics.fill(x + width + 1, y - 1, x + width + 2, y + height + 1, shadowColor); // 右侧阴影

        int spikeCount = 5;
        int spikeWidth = 2;
        for (int i = 0; i < spikeCount; i++) {
            float pos = (float)i / (spikeCount - 1);
            int spikeX = x + (int)(width * pos);
            int spikeHeight = 4 + (int)(Math.sin(pos * Math.PI * 2  * 0.02) * 3); // 高度增加

            // 左侧尖刺（加宽版）
            guiGraphics.fill(spikeX - spikeWidth, y - spikeHeight, spikeX, y, borderColor);

            // 右侧尖刺（加宽版）
            guiGraphics.fill(spikeX - spikeWidth, y + height, spikeX, y + height + spikeHeight, borderColor);

        }

        int xThickness = 2; // 斜线粗细
        int xLength = 10;   // 斜线长度

        // 左上到右下斜线
        drawDiagonalLine(guiGraphics,
                x, y - 1,
                x + xLength, y - 1 - xLength/2,
                xThickness, borderColor);

        // 右上到左下斜线
        drawDiagonalLine(guiGraphics,
                x + width, y - 1,
                x + width - xLength, y - 1 - xLength/2,
                xThickness, borderColor);

        // 左下到右上斜线
        drawDiagonalLine(guiGraphics,
                x, y + height + 1,
                x + xLength, y + height + 1 + xLength/2,
                xThickness, borderColor);

        // 右下到左上斜线
        drawDiagonalLine(guiGraphics,
                x + width, y + height + 1,
                x + width - xLength, y + height + 1 + xLength/2,
                xThickness, borderColor);
    }

    // 优化的斜线绘制方法
    private static void drawDiagonalLine(GuiGraphics guiGraphics,
                                         int x1, int y1, int x2, int y2, int thickness, int color) {

        // 确保从左到右绘制
        if (x1 > x2) {
            int tx = x1; x1 = x2; x2 = tx;
            int ty = y1; y1 = y2; y2 = ty;
        }

        float slope = (float)(y2 - y1) / (x2 - x1);
        for (int x = x1; x <= x2; x++) {
            int y = y1 + (int)(slope * (x - x1));
            // 绘制有厚度的线
            for (int t = 0; t < thickness; t++) {
                guiGraphics.fill(x, y + t, x + 1, y + t + 1, color);
            }
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
                int y = event.getY() + 10; // 调整位置

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
