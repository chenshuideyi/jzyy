package com.csdy.jzyy.entity.monster.model;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DogJiaoModel extends GeoModel<DogJiao> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/dog_jiao.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/dog_jiao.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/dog_jiao.animation.json");



    @Override
    public ResourceLocation getModelResource(DogJiao dogJiao) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(DogJiao dogJiao) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(DogJiao dogJiao) {
        return animations;
    }
}

