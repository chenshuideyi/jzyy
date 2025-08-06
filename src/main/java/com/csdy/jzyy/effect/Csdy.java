package com.csdy.jzyy.effect;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Csdy extends MobEffect {

    public Csdy() {
        super(MobEffectCategory.NEUTRAL, 0);
    }


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.hasEffect(JzyyEffectRegister.CSDY.get())) {
                float turnSpeed = 75.0F;

                float currentYaw = player.getYRot();

                float newYaw = currentYaw + turnSpeed;

                player.setYRot(newYaw);
                player.setYHeadRot(newYaw);
            }
        }
    }
}
