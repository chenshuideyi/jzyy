package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.SwordManCsdy;
import com.csdy.jzyy.entity.boss.model.JzyyEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JzyyEntityRenderer extends GeoEntityRenderer<SwordManCsdy> {

        public JzyyEntityRenderer(EntityRendererProvider.Context context) {
            super(context, new JzyyEntityModel());
        }
    }