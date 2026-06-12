package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.GoldMcCree;
import com.csdy.jzyy.entity.boss.model.GoldMcCreeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GoldMcCreeRenderer extends GeoEntityRenderer<GoldMcCree> {

    public GoldMcCreeRenderer(EntityRendererProvider.Context context) {
            super(context, new GoldMcCreeModel());
        }

}