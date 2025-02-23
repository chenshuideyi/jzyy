package com.csdy.until.ReClass;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;

import java.util.Random;

public class RenderGui extends Gui {

    private final Minecraft mc = Minecraft.getInstance();

    private final int width = mc.mainRenderTarget.viewWidth;
    private final int height = mc.mainRenderTarget.viewHeight;


    public RenderGui(Minecraft p_232355_, ItemRenderer p_232356_) {
        super(p_232355_, p_232356_);
    }


    private static long milliTime() {
        return System.nanoTime() / 150000000L;
    }

    public void render(GuiGraphics guiGraphics, float p_282611_) {
        guiGraphics.fillGradient(RenderType.gui(), 0, 0, this.width, this.height, new Random().nextInt(), new Random().nextInt(), 0);
        guiGraphics.drawCenteredString (mc.font, Component.translatable("deathScreen.title"), this.width / 2 / 2, 30, 16777215);
        Component component = Component.translatable("deathScreen.respawn");
        Button button = Button.builder(component, (p_280794_) -> {
        }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build();
        Component component1 = Component.translatable("deathScreen.quit.confirm");
        Button button1 = Button.builder(component, (p_280794_) -> {
        }).bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build();
        button1.render(guiGraphics, 0, 0, 0.0F);
        button.render(guiGraphics, 0, 0, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.pose().pushPose();
    }
}
