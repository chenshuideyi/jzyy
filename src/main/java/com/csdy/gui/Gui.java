package com.csdy.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;

import javax.annotation.Nullable;
import java.awt.*;

public class Gui extends net.minecraft.client.gui.Gui {
    Minecraft mc = Minecraft.getInstance();
    private int width = mc.mainRenderTarget.viewWidth;
    private int height = mc.mainRenderTarget.viewHeight;
    @Nullable
    private Button exitToTitleButton;

    public Gui(Minecraft mc, ItemRenderer renderer) {
        super(mc, renderer);
        this.width = this.mc.window.getWidth();
        this.height = this.mc.window.getHeight();
    }

    public void render(GuiGraphics guiGraphics, int p_283551_, int p_283002_, float p_281981_) {

        guiGraphics.fill(0, 0, width, height, 0xFF000000); // ARGB格式：0x80表示50%透明度，000000表示黑色

    }
}