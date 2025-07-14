package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.SwordManCsdy;
import com.csdy.jzyy.entity.boss.model.SwordManCsdyModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SwordManCsdyRenderer extends GeoEntityRenderer<SwordManCsdy> {

        public SwordManCsdyRenderer(EntityRendererProvider.Context context) {
            super(context, new SwordManCsdyModel());
        }
    }