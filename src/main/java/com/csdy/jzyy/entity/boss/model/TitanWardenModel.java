package com.csdy.jzyy.entity.boss.model;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.entity.TitanWarden;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TitanWardenModel extends GeoModel<TitanWarden> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/titan_warden.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/titan_warden.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/titan_warden.animation.json");

    @Override
    public ResourceLocation getModelResource(TitanWarden miziAo) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(TitanWarden miziAo) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(TitanWarden miziAo) {
        return animations;
    }
}
