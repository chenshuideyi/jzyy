package com.csdy.jzyy.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    @Overwrite
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Overwrite
    private void handleExitToTitleScreen() {
    }

    @Overwrite
    private void exitToTitleScreen() {
    }

    @Overwrite
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        mc.screen = null;
        mc.mouseHandler.grabMouse();
    }

    @Overwrite
    public void tick() {
    }
}
