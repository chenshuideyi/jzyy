package com.csdy.jzyy.cheat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FunctionSet {

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player && UI.NoFallDamage) {
            if (event.getSource().is(DamageTypes.FALL)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player && UI.NoFallDamage) {
            event.setDistance(0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerLand(LivingFallEvent event) {
        if (event.getEntity() instanceof Player && UI.NoFallDamage) {
            event.setDamageMultiplier(0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerFallWithThreshold(LivingFallEvent event) {
        if (event.getEntity() instanceof Player && UI.NoFallDamage) {
            float fallDistance = event.getDistance();
            if (fallDistance > 0) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerKeyPress(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Minecraft mc = Minecraft.getInstance();
        if (!UI.HeadOutAccelerate || !mc.options.keyAttack.isDown() || !mc.player.getMainHandItem().isEmpty()) {
            return;
        }
        LocalPlayer player = mc.player;
        if (player != null) {
            player.moveTo(player.getX() + player.getLookAngle().x, player.getY(), player.getZ() + player.getLookAngle().z);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (UI.Fly) {
                    mc.player.getAbilities().mayfly = true;
                }
//                else {
//                    if (!mc.player.isCreative()) {
//                        mc.player.getAbilities().mayfly = false;
//                        mc.player.getAbilities().flying = false;
//                    }
//                }
                if (UI.Speed) {
                    mc.player.getAbilities().setFlyingSpeed(0.3f);
                } else {
                    mc.player.getAbilities().setFlyingSpeed(0.1f);
                }
            }
        }
    }
}
