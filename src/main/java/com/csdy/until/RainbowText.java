package com.csdy.until;

import net.minecraft.ChatFormatting;

import java.util.Random;

public class RainbowText {
    private static final ChatFormatting[] colour;
    private static final ChatFormatting[] colour2;
    private static final ChatFormatting[] colour3;
    private static final ChatFormatting[] colour4;
    private static final ChatFormatting[] colour5;
    private static final ChatFormatting[] colour6;
    private static final ChatFormatting[] colour7;
    public RainbowText() {
    }

    public static String formatting(String input, ChatFormatting[] colours, double delay) {
        StringBuilder sb = new StringBuilder(input.length() * 3);
        if (delay <= 0.0) {
            delay = 0.001;
        }

        int offset = (int)Math.floor((double)(System.currentTimeMillis() & 16383L) / delay) % colours.length;

        for(int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            sb.append(colours[(colours.length + i - offset) % colours.length].toString());
            sb.append(c);
        }

        return sb.toString();
    }
    public static String makeRainbowRC(String input) {
        int length = input.length();
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < length; ++i) {
            str.append("qwertyuiopasdfghjklzxcvbnm12345678900".charAt((new Random()).nextInt("qwertyuiopasdfghjklzxcvbnm12345678900".length())));
        }

        return setColour(str.toString());
    }

    public static String setColour(String input) {
        return formatting(input, colour, 1);
    }

    public static String makeColour(String input) {
        return formatting(input, colour, 80.0);
    }

    public static String makeColour2(String input) {
        return formatting(input, colour2, 80.0);
    }
    public static String makeColour3(String input) {
        return formatting(input, colour3, 75.0);
    }
    public static String makeColour4(String input) {
        return formatting(input, colour4, 75.0);
    }
    public static String makeColour5(String input) {
        return formatting(input, colour5, 70.0);
    }
    public static String makeColour6(String input) {
        return formatting(input, colour6, 770.0);
    }
    public static String makeColour7(String input) {
        return formatting(input, colour7, 120.0);
    }
    static {
        colour = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE};
        colour2 = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.LIGHT_PURPLE,ChatFormatting.AQUA};
        colour3 = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.DARK_PURPLE,ChatFormatting.LIGHT_PURPLE};
        colour4 = new ChatFormatting[]{ChatFormatting.DARK_AQUA,ChatFormatting.DARK_RED,ChatFormatting.BLUE};
        colour5 = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.GOLD,ChatFormatting.YELLOW,ChatFormatting.GREEN,ChatFormatting.AQUA,ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE};
        colour6 = new ChatFormatting[]{ChatFormatting.BLUE};
        colour7 = new ChatFormatting[]{ChatFormatting.OBFUSCATED};
    }
}
