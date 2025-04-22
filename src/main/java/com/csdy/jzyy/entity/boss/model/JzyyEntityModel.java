package com.csdy.jzyy.entity.boss.model;


import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.entity.boss.SwordManCsdy;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JzyyEntityModel extends GeoModel<SwordManCsdy> {
    private final ResourceLocation model = new ResourceLocation(ModMain.MODID, "geo/sword_man_csdy.geo.json");
    private final ResourceLocation texture = new ResourceLocation(ModMain.MODID, "textures/entity/sword_man_csdy.png");
    private final ResourceLocation animations = new ResourceLocation(ModMain.MODID, "animations/sword_man_csdy.json");

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
