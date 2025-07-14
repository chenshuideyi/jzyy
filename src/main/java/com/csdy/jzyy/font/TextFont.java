package com.csdy.jzyy.font;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.awt.*;

public class TextFont {
    public static MutableComponent rgb(String text) {
        MutableComponent result = Component.empty();
        long time = System.currentTimeMillis();
        int baseHue = (int) ((time / 10) % 360);
        for (int i = 0; i < text.length(); i++) {
            int hue = (baseHue + i * 15) % 360;
            int rgb = Color.HSBtoRGB(hue / 360.0f, 1.0f, 1.0f);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(rgb));
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }
        return result;
    }
}
