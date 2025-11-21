package com.csdy.jzyy.entity.boss.model;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.entity.GoldMcCree;
import com.csdy.jzyy.entity.boss.entity.MiziAo;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GoldMcCreeModel extends GeoModel<GoldMcCree> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/gold_mccree.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/gold_mccree.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/gold_mccree.animation.json");

    @Override
    public ResourceLocation getModelResource(GoldMcCree goldMcCree) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GoldMcCree goldMcCree) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GoldMcCree goldMcCree) {
        return animations;
    }
}
