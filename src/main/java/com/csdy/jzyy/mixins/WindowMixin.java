package com.csdy.jzyy.mixins;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(
        value = {Window.class},
        priority = Integer.MAX_VALUE
)
public class WindowMixin {
    @Unique
    private static final String[] jzyy$ARRAY = {
            "工匠：技艺革新",
            "泰拉瑞亚",
            "迷你世界",
            "roblox",
            "舞萌DX",
            "中二节奏",
            "pjsk",
            "scp",
            "backrooms",
            "eratw",
            "杀戮尖塔",
            "rimworld",
            "密教模拟器",
            "城堡破坏者",
            "BlackSoulsII",
            "brotato",
            "太吾绘卷",
            "勇者大战魔物娘",
            "暗黑地牢",
            "Fear&Hunger",
            "羽翼的祈愿",
            "游戏王",
            "牧场物语·符文工房",
            "火焰纹章",
            "魔兽世界",
            "挺进地牢",
            "elona"
    };

    @Unique
    private final Random jzyy$random = new Random();
    @Unique
    private String jzyy$currentSubtitle = "";

    public WindowMixin() {
    }

    /**
     * @author
     * @reason 自定义窗口标题并添加随机副标题
     */
    @Overwrite
    public void setTitle(String string) {
        jzyy$currentSubtitle = jzyy$ARRAY[jzyy$random.nextInt(jzyy$ARRAY.length)];
        GLFW.glfwSetWindowTitle(Minecraft.getInstance().window.getWindow(), "匠战妖域 试试" + jzyy$currentSubtitle +" ，很好玩！");
    }
}
