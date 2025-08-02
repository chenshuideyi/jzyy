package com.csdy.jzyy.entity.monster.render;


import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import com.csdy.jzyy.entity.monster.model.HJMModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HJMRenderer extends GeoEntityRenderer<HJMEntity> {
	public HJMRenderer(EntityRendererProvider.Context context) {
		super(context, new HJMModel());
	}
}
