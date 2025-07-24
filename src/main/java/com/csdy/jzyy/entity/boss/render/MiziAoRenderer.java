package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.MiziAo;
import com.csdy.jzyy.entity.boss.model.MiziAoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MiziAoRenderer extends GeoEntityRenderer<MiziAo> {

        public MiziAoRenderer(EntityRendererProvider.Context context) {
            super(context, new MiziAoModel());
        }
    }