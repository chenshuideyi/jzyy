package com.csdy.until.ReClass;

import com.csdy.until.List.DeadLists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class LoveEntityRenderDispatcher extends EntityRenderDispatcher {
    public LoveEntityRenderDispatcher(Minecraft p_234579_, TextureManager p_234580_, ItemRenderer p_234581_, BlockRenderDispatcher p_234582_, Font p_234583_, Options p_234584_, EntityModelSet p_234585_) {
        super(p_234579_, p_234580_, p_234581_, p_234582_, p_234583_, p_234584_, p_234585_);
    }

    @Override
    public <E extends Entity> void render(E p_114385_, double p_114386_, double p_114387_, double p_114388_, float p_114389_, float p_114390_, PoseStack p_114391_, MultiBufferSource p_114392_, int p_114393_) {
        if (DeadLists.isban(p_114385_)){
            return;
        }
        super.render(p_114385_, p_114386_, p_114387_, p_114388_, p_114389_, p_114390_, p_114391_, p_114392_, p_114393_);
    }

    @Override
    public <E extends Entity> boolean shouldRender(E p_114398_, Frustum p_114399_, double p_114400_, double p_114401_, double p_114402_) {
        if (DeadLists.isban(p_114398_)){
            return false;
        }
        return super.shouldRender(p_114398_, p_114399_, p_114400_, p_114401_, p_114402_);
    }

    @Override
    public Map<String, EntityRenderer<? extends Player>> getSkinMap() {
        return super.getSkinMap();
    }

}
