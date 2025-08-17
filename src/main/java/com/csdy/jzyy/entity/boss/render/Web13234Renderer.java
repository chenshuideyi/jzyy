package com.csdy.jzyy.entity.boss.render;

import com.csdy.jzyy.entity.boss.entity.SwordManCsdy;
import com.csdy.jzyy.entity.boss.entity.Web13234;
import com.csdy.jzyy.entity.boss.model.SwordManCsdyModel;
import com.csdy.jzyy.entity.boss.model.Web13234Model;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Web13234Renderer extends GeoEntityRenderer<Web13234> {

    public Web13234Renderer(EntityRendererProvider.Context context) {
        super(context, new Web13234Model());
    }
}
