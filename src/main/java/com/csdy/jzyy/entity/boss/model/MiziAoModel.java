package com.csdy.jzyy.entity.boss.model;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.entity.MiziAo;
import com.csdy.jzyy.entity.boss.entity.SwordManCsdy;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MiziAoModel extends GeoModel<MiziAo> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/mizi_ao.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/mizi_ao.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/mizi_ao.animation.json");

    @Override
    public ResourceLocation getModelResource(MiziAo miziAo) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MiziAo miziAo) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MiziAo miziAo) {
        return animations;
    }
}
