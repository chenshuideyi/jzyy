package com.csdy.jzyy.entity.monster.model;

import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.csdy.jzyy.JzyyModMain.MODID;


public class HJMModel extends GeoModel<HJMEntity> {
	private final ResourceLocation model = new ResourceLocation(MODID, "geo/hjm.geo.json");
	private final ResourceLocation texture = new ResourceLocation(MODID, "textures/entity/hjm.png");
	private final ResourceLocation animations = new ResourceLocation(MODID, "animations/hjm.animation.json");

	
	@Override
	public ResourceLocation getModelResource(HJMEntity hjm) {
		return model;
	}

	@Override
	public ResourceLocation getTextureResource(HJMEntity hjm) {
		return texture;
	}

	@Override
	public ResourceLocation getAnimationResource(HJMEntity hjm) {
		return animations;
	}

}