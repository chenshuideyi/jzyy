package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.DogJiaoJiaoJiao;
import com.csdy.jzyy.entity.boss.model.DogJiaoJiaoJiaoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DogJiaoJiaoJiaoRenderer extends GeoEntityRenderer<DogJiaoJiaoJiao> {

    public DogJiaoJiaoJiaoRenderer(EntityRendererProvider.Context context) {
        super(context, new DogJiaoJiaoJiaoModel());
    }
}
