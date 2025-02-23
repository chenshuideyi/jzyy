package com.csdy.mc;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
@OnlyIn(Dist.CLIENT)
public class CsdyEntityRenderer<T extends Entity> {

    protected CsdyEntityRenderer(EntityRendererProvider.Context p_174008_) {
    }

    public void render(EntityRenderer entityRenderer, T p_114485_, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
        RenderNameTagEvent renderNameTagEvent = new RenderNameTagEvent(p_114485_, p_114485_.getDisplayName(), entityRenderer, p_114488_, p_114489_, p_114490_, p_114487_);
        MinecraftForge.EVENT_BUS.post(renderNameTagEvent);
        if (renderNameTagEvent.getResult() != Result.DENY && (renderNameTagEvent.getResult() == Result.ALLOW || entityRenderer.shouldShowName(p_114485_))) {
            entityRenderer.renderNameTag(p_114485_, renderNameTagEvent.getContent(), p_114488_, p_114489_, p_114490_);
        }

    }
}
