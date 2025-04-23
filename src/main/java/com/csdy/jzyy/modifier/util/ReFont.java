package com.csdy.jzyy.modifier.util;


import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;


public class ReFont extends Font {
    // 字体大小变化的配置常量
    private static final float WAVE_AMPLITUDE = 3.0f;
    private static final float WAVE_SPEED = 0.002f;
    private static final float WAVE_FREQUENCY = 0.5f;
    private static final float COLOR_CYCLE_SPEED = 0.0002f;
    private static final float SATURATION = 0.9f;
    private static final float BRIGHTNESS = 0.9f;
    private static final float CHAR_SPACING = 1.0f;
    private static final float RAINBOW_OFFSET_FACTOR = 0.1f;

    // 新增的大小变化参数
    private static final float SIZE_OSCILLATION_AMPLITUDE = 0.2f; // 大小变化幅度 (20%)
    private static final float SIZE_OSCILLATION_SPEED = 0.0015f; // 大小变化速度
    private static final float BASE_SCALE = 1.0f; // 基础大小

    // 用于存储每个字符的大小状态
    private final Map<Character, Float> charScales = new HashMap<>();

    public ReFont(Function<ResourceLocation, FontSet> fontGetter, boolean filter) {
        super(fontGetter, filter);
    }

    public static ReFont getFont() {
        return new ReFont(Minecraft.getInstance().font.fonts, false);
    }

    @Override
    public int drawInBatch(@NotNull FormattedCharSequence text, float x, float y, int rgb,
                           boolean dropShadow, @NotNull Matrix4f matrix,
                           @NotNull MultiBufferSource buffer,
                           @NotNull DisplayMode displayMode,
                           int backgroundColor, int packedLightCoords) {

        String plainText = extractPlainText(text);
        if (plainText == null || plainText.isEmpty()) {
            return (int) x;
        }

        long time = Util.getMillis();
        float baseHue = (time * COLOR_CYCLE_SPEED) % 1.0f;
        float waveTime = time * WAVE_SPEED;

        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);
            String charStr = String.valueOf(c);

            // 计算波动效果
            float waveOffset = (float) Math.sin(waveTime + i * WAVE_FREQUENCY) * WAVE_AMPLITUDE;

            // 计算彩虹颜色
            float charHue = (baseHue + i * RAINBOW_OFFSET_FACTOR) % 1.0f;
            int charColor = calculateColor(charHue, SATURATION, BRIGHTNESS, rgb);

            // 计算平滑的大小变化
            float sizeScale = calculateSizeScale(time, i, c);

            // 创建缩放矩阵
            Matrix4f scaledMatrix = new Matrix4f(matrix);
            scaledMatrix.translate(x, y + waveOffset, 0);
            scaledMatrix.scale(sizeScale, sizeScale, 1.0f);
            scaledMatrix.translate(-x, -(y + waveOffset), 0);

            // 绘制字符
            super.drawInBatch(charStr, x, y + waveOffset, charColor,
                    dropShadow, scaledMatrix, buffer,
                    displayMode, backgroundColor, packedLightCoords);

            // 更新字符间距（考虑缩放因素）
            x += this.width(charStr) * CHAR_SPACING * sizeScale;
        }

        return (int) x;
    }

    // 计算平滑的大小变化
    private float calculateSizeScale(long time, int charIndex, char c) {
        // 为每个字符创建独立但相关的缩放动画
        float phase = (time * SIZE_OSCILLATION_SPEED) + (charIndex * 0.3f);

        // 使用正弦波创建平滑的脉动效果
        float oscillation = (float) Math.sin(phase) * SIZE_OSCILLATION_AMPLITUDE;

        // 计算最终缩放比例
        return BASE_SCALE + oscillation;
    }

    @Override
    public int drawInBatch(@NotNull String text, float x, float y, int color,
                           boolean dropShadow, @NotNull Matrix4f matrix,
                           @NotNull MultiBufferSource buffer,
                           @NotNull DisplayMode displayMode,
                           int backgroundColor, int packedLightCoords) {
        return this.drawInBatch(Component.literal(text).getVisualOrderText(),
                x, y, color, dropShadow,
                matrix, buffer, displayMode,
                backgroundColor, packedLightCoords);
    }

    @Override
    public int drawInBatch(@NotNull Component component, float x, float y, int color,
                           boolean dropShadow, @NotNull Matrix4f matrix,
                           @NotNull MultiBufferSource buffer,
                           @NotNull DisplayMode displayMode,
                           int backgroundColor, int packedLightCoords) {
        return this.drawInBatch(component.getVisualOrderText(),
                x, y, color, dropShadow,
                matrix, buffer, displayMode,
                backgroundColor, packedLightCoords);
    }

    // Helper methods (保持不变)
    private String extractPlainText(FormattedCharSequence text) {
        StringBuilder builder = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return ChatFormatting.stripFormatting(builder.toString());
    }

    private int calculateColor(float hue, float saturation, float brightness, int baseColor) {
        int alpha = baseColor & 0xFF000000;
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return alpha | (rgb & 0x00FFFFFF);
    }
}