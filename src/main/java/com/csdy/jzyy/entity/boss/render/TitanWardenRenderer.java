package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.TitanWarden;
import com.csdy.jzyy.entity.boss.model.TitanWardenModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TitanWardenRenderer extends GeoEntityRenderer<TitanWarden> {

        public TitanWardenRenderer(EntityRendererProvider.Context context) {
            super(context, new TitanWardenModel());
        }
    }