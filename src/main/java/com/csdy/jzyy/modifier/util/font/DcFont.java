package com.csdy.jzyy.modifier.util.font;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Function;

public class DcFont extends Font {
    public DcFont(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
        super(p_243253_, p_243245_);
    }

    public static DcFont getFont() {
        return new DcFont(Minecraft.getInstance().font.fonts, false);
    }

    public int drawInBatch(@NotNull FormattedCharSequence formattedCharSequence, float x, float y, int rgb, boolean b1, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource multiBufferSource, @NotNull Font.@NotNull DisplayMode mode, int i, int i1) {
        StringBuilder builder = new StringBuilder();
        formattedCharSequence.accept((p_13746_, p_13747_, p_13748_) -> {
            builder.appendCodePoint(p_13748_);
            return true;
        });
        return this.renderFont(builder.toString(), x, y, rgb, b1, matrix4f, multiBufferSource, mode, i, i1, this.isBidirectional());
    }

    public int drawInBatch(@NotNull String text, float x, float y, int rgb, boolean b, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource source, @NotNull Font.@NotNull DisplayMode mode, int i, int i1) {
        return this.renderFont(text, x, y, rgb, b, matrix4f, source, mode, i, i1, this.isBidirectional());
    }

    public int drawInBatch(@NotNull Component component, float x, float y, int rgb, boolean b, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource source, @NotNull Font.@NotNull DisplayMode mode, int i, int i1) {
        return this.renderFont(component.getString(), x, y, rgb, b, matrix4f, source, mode, i, i1, this.isBidirectional());
    }

    public int renderFont(@NotNull String text, float x, float y, int rgb, boolean dropShadow, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource multiBufferSource, @NotNull Font.@NotNull DisplayMode mode, int i, int i1, boolean isText) {
        float hueOffset = (float) Util.getMillis() / 800.0F;

        for (int index = 0; index < text.length(); ++index) {
            String s = String.valueOf(text.charAt(index));
            float offset_y = (float) ((double) y + Math.sin((double) ((float) Util.getMillis() / 500.0F + (float) index / 5.0F)) * 3.0);
            float hue = (hueOffset + (float) index / (float) text.length()) % 1.0F;
            int c = rgb & -16777216 | Mth.hsvToRgb(hue, 0.8F, 1.0F);
            super.drawInBatch(s, x, offset_y, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, x + 0.852F, offset_y, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            x += (float) this.width(s);
        }
        return (int)x;
    }
}
