package com.csdy.until.ReClass;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FuckGameRender extends GameRenderer {
    public FuckGameRender(Minecraft p_234219_, ItemInHandRenderer p_234220_, ResourceManager p_234221_, RenderBuffers p_234222_) {
        super(p_234219_, p_234220_, p_234221_, p_234222_);
    }

    @Nullable
    @Override
    public ShaderInstance getShader(@Nullable String p_172735_) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui = new RenderGui(mc,mc.getItemRenderer());
        return super.getShader(p_172735_);
    }

    @Override
    public float getRenderDistance() {
        return super.getRenderDistance();
    }

    @Override
    public void renderLevel(float p_109090_, long p_109091_, PoseStack p_109092_) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui = new RenderGui(mc,mc.getItemRenderer());
        super.renderLevel(p_109090_, p_109091_, p_109092_);
    }

    @Override
    public void displayItemActivation(ItemStack p_109114_) {
        super.displayItemActivation(p_109114_);
    }
}
