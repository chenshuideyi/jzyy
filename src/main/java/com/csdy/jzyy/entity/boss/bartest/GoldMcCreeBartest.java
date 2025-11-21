package com.csdy.jzyy.entity.boss.bartest;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GoldMcCreeBartest {

    public static final ResourceLocation VAMPIRE_BAR_BACKGROUND =
            new ResourceLocation("jzyy", "textures/gui/vampire_bar_background.png");


    static Minecraft mc = Minecraft.getInstance();
    static Window window = mc.getWindow();
    public static int testX = 0;
    public static int testY = 0;

    // 吸血鬼主题颜色
    private static final int BLOOD_RED = rgba(180, 0, 0, 255);
    private static final int DARK_BLOOD = rgba(120, 0, 0, 255);
    private static final int BLOOD_CRIMSON = rgba(220, 20, 20, 255);
    private static final int BORDER_COLOR = rgba(80, 0, 0, 220);
    private static final int SHADOW_COLOR = rgba(30, 0, 0, 150);
    private static final int GOLD_ACCENT = rgba(200, 160, 50, 255);

    public static int rgba(int red, int green, int blue, int alpha) {
        alpha %= 256;
        red %= 256;
        green %= 256;
        blue %= 256;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // 血液效果的渐变填充
    public static void fillBloodEffect(GuiGraphics guiGraphics, int x, int y, int width, int height, float healthPercentage) {
        // 血液主色填充
        guiGraphics.fill(x, y, x + width, y + height, BLOOD_RED);

        // 血液纹理效果 - 深色条纹
        int stripeCount = Math.max(3, (int)(width * 0.1f));
        for (int i = 0; i < stripeCount; i++) {
            int stripeX = x + (i * width / stripeCount);
            int stripeWidth = 2 + (int)(Math.sin(System.currentTimeMillis() * 0.002 + i) * 1);
            guiGraphics.fill(stripeX, y, stripeX + stripeWidth, y + height, DARK_BLOOD);
        }

        // 血液光泽效果
        if (width > 10) {
            int highlightHeight = height / 2;
            for (int i = 0; i < width; i += 2) {
                float wave = (float)Math.sin((System.currentTimeMillis() * 0.003) + i * 0.1) * 0.3f + 0.7f;
                int highlightColor = rgba(255, 50, 50, (int)(100 * wave));
                guiGraphics.fill(x + i, y, x + i + 1, y + highlightHeight, highlightColor);
            }
        }

        // 低血量时的脉动效果
        if (healthPercentage < 0.3f) {
            float pulse = (float)Math.sin(System.currentTimeMillis() * 0.005) * 0.2f + 0.8f;
            int pulseColor = rgba(255, 100, 100, (int)(80 * (1 - healthPercentage)));
            guiGraphics.fill(x, y, x + width, y + height, pulseColor);
        }
    }

    // 绘制吸血鬼风格的边框
    public static void drawVampireBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        // 主边框（血红色）
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y, BORDER_COLOR);         // 上边框
        guiGraphics.fill(x - 1, y + height, x + width + 1, y + height + 1, BORDER_COLOR); // 下边框
        guiGraphics.fill(x - 1, y, x, y + height, BORDER_COLOR);                // 左边框
        guiGraphics.fill(x + width, y, x + width + 1, y + height, BORDER_COLOR);// 右边框

        // 阴影效果
        guiGraphics.fill(x - 2, y - 2, x + width + 2, y - 1, SHADOW_COLOR);    // 顶部阴影
        guiGraphics.fill(x - 2, y + height + 1, x + width + 2, y + height + 2, SHADOW_COLOR);
        guiGraphics.fill(x - 2, y - 1, x - 1, y + height + 1, SHADOW_COLOR);
        guiGraphics.fill(x + width + 1, y - 1, x + width + 2, y + height + 1, SHADOW_COLOR);

        // 蝙蝠翅膀风格的尖刺
        drawBatWingSpikes(guiGraphics, x, y, width, height);

        // 吸血鬼牙齿装饰
        drawVampireTeeth(guiGraphics, x, y, width, height);

        // 金色装饰角
        drawGoldAccents(guiGraphics, x, y, width, height);
    }

    // 绘制蝙蝠翅膀风格的尖刺
    private static void drawBatWingSpikes(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int spikeCount = 7; // 增加尖刺数量
        int maxSpikeHeight = 8;

        for (int i = 0; i < spikeCount; i++) {
            float pos = (float)i / (spikeCount - 1);

            // 蝙蝠翅膀形状的尖刺 - 不对称设计
            int baseHeight = 4;
            int dynamicHeight = baseHeight + (int)(Math.sin(pos * Math.PI * 3 + System.currentTimeMillis() * 0.001) * 2);

            // 左侧尖刺（蝙蝠翅膀形状）
            int leftSpikeX = x + (int)(width * pos * 0.8f);
            int leftSpikeHeight = Math.min(maxSpikeHeight, dynamicHeight + (int)(Math.sin(pos * 2) * 2));
            drawBatSpike(guiGraphics, leftSpikeX, y, leftSpikeHeight, true);

            // 右侧尖刺
            int rightSpikeX = x + (int)(width * (0.2f + pos * 0.8f));
            int rightSpikeHeight = Math.min(maxSpikeHeight, dynamicHeight + (int)(Math.cos(pos * 2) * 2));
            drawBatSpike(guiGraphics, rightSpikeX, y + height, rightSpikeHeight, false);
        }
    }

    // 绘制单个蝙蝠尖刺
    private static void drawBatSpike(GuiGraphics guiGraphics, int x, int y, int height, boolean top) {
        int spikeWidth = 3;
        int color = BORDER_COLOR;

        if (top) {
            // 顶部尖刺（向下）
            for (int i = 0; i < height; i++) {
                int currentWidth = (int)(spikeWidth * (1 - (float)i / height));
                guiGraphics.fill(x - currentWidth/2, y - i - 1, x + currentWidth/2 + 1, y - i, color);
            }
        } else {
            // 底部尖刺（向上）
            for (int i = 0; i < height; i++) {
                int currentWidth = (int)(spikeWidth * (1 - (float)i / height));
                guiGraphics.fill(x - currentWidth/2, y + i + 1, x + currentWidth/2 + 1, y + i + 2, color);
            }
        }
    }

    // 绘制吸血鬼牙齿装饰
    private static void drawVampireTeeth(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int toothCount = 5;
        int toothColor = rgba(240, 240, 240, 255);
        int fangColor = rgba(220, 220, 255, 255);

        for (int i = 0; i < toothCount; i++) {
            float pos = (float)i / (toothCount - 1);
            int toothX = x + 10 + (int)((width - 20) * pos);

            // 普通牙齿（较小）
            int toothHeight = 3;
            guiGraphics.fill(toothX - 1, y - toothHeight - 1, toothX + 1, y - 1, toothColor);
            guiGraphics.fill(toothX - 1, y + height + 1, toothX + 1, y + height + toothHeight + 1, toothColor);

            // 獠牙（较大，在两侧）
            if (i == 0 || i == toothCount - 1) {
                int fangHeight = 6;
                int fangWidth = 2;
                if (i == 0) {
                    // 左侧獠牙
                    guiGraphics.fill(x - 3, y - fangHeight - 1, x, y - 1, fangColor);
                    guiGraphics.fill(x - 3, y + height + 1, x, y + height + fangHeight + 1, fangColor);
                } else {
                    // 右侧獠牙
                    guiGraphics.fill(x + width, y - fangHeight - 1, x + width + 3, y - 1, fangColor);
                    guiGraphics.fill(x + width, y + height + 1, x + width + 3, y + height + fangHeight + 1, fangColor);
                }
            }
        }
    }

    // 绘制金色装饰
    private static void drawGoldAccents(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        // 四角的金色装饰
        int accentSize = 3;

        // 左上角
        guiGraphics.fill(x - 2, y - 2, x + accentSize - 2, y - 1, GOLD_ACCENT);
        guiGraphics.fill(x - 2, y - 2, x - 1, y + accentSize - 2, GOLD_ACCENT);

        // 右上角
        guiGraphics.fill(x + width - accentSize + 2, y - 2, x + width + 2, y - 1, GOLD_ACCENT);
        guiGraphics.fill(x + width + 1, y - 2, x + width + 2, y + accentSize - 2, GOLD_ACCENT);

        // 左下角
        guiGraphics.fill(x - 2, y + height + 1, x + accentSize - 2, y + height + 2, GOLD_ACCENT);
        guiGraphics.fill(x - 2, y + height - accentSize + 2, x - 1, y + height + 2, GOLD_ACCENT);

        // 右下角
        guiGraphics.fill(x + width - accentSize + 2, y + height + 1, x + width + 2, y + height + 2, GOLD_ACCENT);
        guiGraphics.fill(x + width + 1, y + height - accentSize + 2, x + width + 2, y + height + 2, GOLD_ACCENT);
    }

    private static void drawVampireBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, VAMPIRE_BAR_BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 计算纹理缩放以适应血条尺寸
        int textureWidth = 128;  // 你的PNG纹理宽度
        int textureHeight = 128;  // 你的PNG纹理高度

        // 绘制纹理，可以平铺或拉伸
        guiGraphics.blit(VAMPIRE_BAR_BACKGROUND,
                x + width/2 - 15 , y - 20,  // 位置（可以调整偏移量）
                width + 30, height,  // 尺寸（可以调整大小）
                0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        LerpingBossEvent bossStatusInfo = event.getBossEvent();

        if (event.getBossEvent().getName().getString().startsWith(Component.translatable("entity.jzyy.gold_mccree").getString())) {
            event.setCanceled(true);

            if (mc.level != null) {
                RenderSystem.enableBlend();
                GuiGraphics guiGraphics = event.getGuiGraphics();

                // 血条尺寸
                int width = 256;
                int height = 10; // 稍微增加高度
                int x = window.getGuiScaledWidth() / 2 - width / 2;
                int y = event.getY() + 10;

                drawVampireBackground(guiGraphics, x, y, width/2, height * 6);

                // 绘制吸血鬼风格边框
                drawVampireBorder(guiGraphics, x, y, width, height);

                // 背景 (暗红色)
                guiGraphics.fill(x, y, x + width, y + height, rgba(40, 0, 0, 200));

                // 血条进度
                float healthPercentage = bossStatusInfo.getProgress();
                int progressWidth = (int) (width * healthPercentage);

                // 使用血液效果填充血条
                if (progressWidth > 0) {
                    fillBloodEffect(guiGraphics, x, y, progressWidth, height, healthPercentage);
                }

                // 绘制Boss名称（吸血鬼风格）
                String bossName = bossStatusInfo.getName().getString();
                Font font = mc.font;

                // 血红色名称，低血量时闪烁
                int nameColor = BLOOD_CRIMSON;
                if (healthPercentage < 0.3f) {
                    float flicker = (float)Math.sin(System.currentTimeMillis() * 0.01) * 0.5f + 0.5f;
                    nameColor = rgba(255, (int)(100 * flicker), (int)(100 * flicker), 255);
                }

                int nameWidth = font.width(bossName);
                int nameX = x + (width / 2) - (nameWidth / 2);
                int nameY = y - 15; // 稍微下移以适应更大的边框

                // 名称背景（蝙蝠翅膀形状）
                int bgPadding = 6;
                int bgHeight = 12;
                guiGraphics.fill(nameX - bgPadding, nameY - 2, nameX + nameWidth + bgPadding, nameY + bgHeight, rgba(20, 0, 0, 200));

                // 名称边框
                guiGraphics.fill(nameX - bgPadding, nameY - 2, nameX + nameWidth + bgPadding, nameY - 1, BORDER_COLOR);
                guiGraphics.fill(nameX - bgPadding, nameY + bgHeight, nameX + nameWidth + bgPadding, nameY + bgHeight + 1, BORDER_COLOR);
                guiGraphics.fill(nameX - bgPadding, nameY - 1, nameX - bgPadding + 1, nameY + bgHeight, BORDER_COLOR);
                guiGraphics.fill(nameX + nameWidth + bgPadding - 1, nameY - 1, nameX + nameWidth + bgPadding, nameY + bgHeight, BORDER_COLOR);

                // 绘制名称
                guiGraphics.drawString(font, bossName, nameX, nameY, nameColor, true);

                RenderSystem.disableBlend();
            }
        }
    }
}
