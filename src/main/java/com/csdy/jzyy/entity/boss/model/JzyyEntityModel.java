package com.csdy.jzyy.entity.boss.model;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.SwordManCsdy;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JzyyEntityModel extends GeoModel<SwordManCsdy> {
    private final ResourceLocation model = new ResourceLocation(JzyyModMain.MODID, "geo/sword_man_csdy.geo.json");
    private final ResourceLocation texture = new ResourceLocation(JzyyModMain.MODID, "textures/entity/sword_man_csdy.png");
    private final ResourceLocation animations = new ResourceLocation(JzyyModMain.MODID, "animations/sword_man_csdy.animation.json");

    @Override
    public ResourceLocation getModelResource(SwordManCsdy swordManCsdy) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SwordManCsdy swordManCsdy) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SwordManCsdy swordManCsdy) {
        return animations;
    }
}
