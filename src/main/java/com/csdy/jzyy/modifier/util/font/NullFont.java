package com.csdy.jzyy.modifier.util.font;


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

public class NullFont extends Font {
    // 死亡主题颜色调色板（暗色系）
    private static final int[] DEATH_PALETTE = {
            0xFF2C3E50,  // 深蓝黑
            0xFF4A235A,  // 暗紫
            0xFF1B2631,  // 暗蓝灰
            0xFF424949,  // 暗灰
            0xFF1C2833,  // 深蓝灰
            0xFF2E4053   // 暗钢蓝
    };

    public NullFont(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
        super(p_243253_, p_243245_);
    }

    public static NullFont getFont() {
        return new NullFont(Minecraft.getInstance().font.fonts, false);
    }

    @Override
    public int drawInBatch(@NotNull FormattedCharSequence formattedCharSequence, float x, float y, int rgb, boolean b1,
                           @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource multiBufferSource,
                           @NotNull DisplayMode mode, int i, int i1) {
        StringBuilder stringBuilder = new StringBuilder();
        formattedCharSequence.accept((index, style, codePoint) -> {
            stringBuilder.appendCodePoint(codePoint);
            return true;
        });

        String text = ChatFormatting.stripFormatting(stringBuilder.toString());
        if (text != null) {
            long currentTime = Util.getMillis();
            float globalTimeFactor = (float) (currentTime % 3000) / 3000.0F; // 3秒周期

            // 整体透明度波动 (忽隐忽现效果)
            float globalAlpha = 0.3f + 0.7f * (float) Math.sin(globalTimeFactor * Math.PI * 2);

            for (int index = 0; index < text.length(); ++index) {
                String s = String.valueOf(text.charAt(index));

                // 字符独立动画参数
                float charTimeOffset = (float) index * 0.2f;
                float charTimeFactor = ((float) (currentTime % 2000) / 2000.0F + charTimeOffset) % 1.0f;



                // 字符透明度 (独立于全局透明度)
                float charAlpha = 0.1F * (float) Math.pow(Math.sin(charTimeFactor * Math.PI), 2);

                // 从死亡调色板中选择颜色
                int baseColor = DEATH_PALETTE[(index + (int)(currentTime/1000)) % DEATH_PALETTE.length];

                // 应用透明度
                int color = applyAlpha(baseColor, globalAlpha * charAlpha);

                // 随机字符消失效果 (10%几率字符暂时消失)
                if (Math.random() > 0.1f) {
                    super.drawInBatch(s, x, y, color, b1, matrix4f, multiBufferSource, mode, i, i1);
                }

                x += (float) this.width(s);
            }
        }
        return (int)x;
    }

    // 应用透明度到颜色
    private int applyAlpha(int color, float alpha) {
        alpha = Math.min(1.0f, Math.max(0.0f, alpha));
        int a = (int)(alpha * 255);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    // 保留其他drawInBatch重载方法
    @Override
    public int drawInBatch(@NotNull String string, float x, float y, int rgb, boolean b,
                           @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource source,
                           @NotNull DisplayMode mode, int i, int i1) {
        return this.drawInBatch(Component.literal(string).getVisualOrderText(), x, y, rgb, b, matrix4f, source, mode, i, i1);
    }

    @Override
    public int drawInBatch(@NotNull Component component, float x, float y, int rgb, boolean b,
                           @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource source,
                           @NotNull DisplayMode mode, int i, int i1) {
        return this.drawInBatch(component.getVisualOrderText(), x, y, rgb, b, matrix4f, source, mode, i, i1);
    }
}