package com.csdy.jzyy.entity.boss.model;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.entity.DogJiaoJiaoJiao;
import com.csdy.jzyy.entity.boss.entity.MiziAo;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DogJiaoJiaoJiaoModel extends GeoModel<DogJiaoJiaoJiao> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/dog_jiao_jiao_jiao.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/dog_jiao_jiao_jiao.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/dog_jiao_jiao_jiao.animation.json");

    @Override
    public ResourceLocation getModelResource(DogJiaoJiaoJiao dogJiaoJiaoJiao) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(DogJiaoJiaoJiao dogJiaoJiaoJiao) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(DogJiaoJiaoJiao dogJiaoJiaoJiao) {
        return animations;
    }
}
