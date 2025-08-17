package com.csdy.jzyy.entity.boss.model;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.entity.Web13234;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Web13234Model extends GeoModel<Web13234> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/Web_13234.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/web_13234.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/web_13234.animation.json");

    @Override
    public ResourceLocation getModelResource(Web13234 web13234) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(Web13234 web13234) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(Web13234 web13234) {
        return animations;
    }
}
