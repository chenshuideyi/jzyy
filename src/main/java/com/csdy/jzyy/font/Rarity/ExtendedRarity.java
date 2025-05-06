package com.csdy.jzyy.font.Rarity;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;

public class ExtendedRarity {
    // 扩展枚举值 - 红色字体的传奇稀有度
    public static final Rarity LEGENDARY = Rarity.create(
            "LEGENDARY",
            ChatFormatting.RED
    );

    // 或者你也可以使用更复杂的样式修改器
    public static final Rarity MYTHIC = Rarity.create(
            "MYTHIC",
            style -> style.withColor(ChatFormatting.RED).withBold(true)
    );


    // 定义动态颜色数组（例如彩虹色）
    private static final ChatFormatting[] RAINBOW_COLORS = {
            ChatFormatting.RED,
            ChatFormatting.GOLD,
            ChatFormatting.YELLOW,
            ChatFormatting.GREEN,
            ChatFormatting.AQUA,
            ChatFormatting.BLUE,
            ChatFormatting.LIGHT_PURPLE
    };

    // 动态颜色稀有度
    public static final Rarity RAINBOW = Rarity.create(
            "RAINBOW",
            style -> {
                // 计算当前时间偏移量
                long time = System.currentTimeMillis();
                int offset = (int) ((time / 80) % RAINBOW_COLORS.length);

                // 应用动态颜色
                return style.withColor(RAINBOW_COLORS[offset]);
            }
    );

    // 更复杂的动态渐变效果（基于字符位置+时间）
    public static final Rarity DYNAMIC_GRADIENT = Rarity.create(
            "DYNAMIC_GRADIENT",
            style -> {
                // 这里需要特殊处理，因为Style不直接支持每个字符的颜色
                // 可能需要配合自定义文本组件使用
                return style.withColor(ChatFormatting.WHITE); // 占位
            }
    );
}

