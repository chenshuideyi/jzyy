package com.csdy.test;


import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class EntityRenderers {
    public EntityRenderers() {
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType) EntityRegister.RAINBOW_LIGHTING.get(), ColorLightningRenderer::new);
        event.registerEntityRenderer((EntityType) EntityRegister.RAINBOW_LIGHTING2.get(), ColorLightningRenderer2::new);
    }
}
