package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.TitanWarden;
import com.csdy.jzyy.entity.boss.model.TitanWardenModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TitanWardenRenderer extends GeoEntityRenderer<TitanWarden> {

    public TitanWardenRenderer(EntityRendererProvider.Context context) {
        super(context, new TitanWardenModel());
    }
    @Override
    public void preRender(PoseStack poseStack, TitanWarden entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        poseStack.scale(30f,30f,30f);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

    }
}