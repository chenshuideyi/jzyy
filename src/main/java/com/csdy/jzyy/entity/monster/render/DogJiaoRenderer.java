package com.csdy.jzyy.entity.monster.render;

import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.entity.monster.model.DogJiaoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DogJiaoRenderer extends GeoEntityRenderer<DogJiao> {

        public DogJiaoRenderer(EntityRendererProvider.Context context) {
            super(context, new DogJiaoModel());
        }
    }