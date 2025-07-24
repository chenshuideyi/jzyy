package com.csdy.jzyy.cheat;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FunctionSet {

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinLevelEvent event) throws AuthenticationUnavailableException {
        if (event.getEntity() instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getSingleplayerServer() != null && isPlayerPremium(mc.getSingleplayerServer(), player.getUUID(), player.getName().getString()) && mc.player.getDisplayName().equals("libLxHook_16384")) {
                player.sendSystemMessage(Component.literal("正版账号验证通过，您是开发者落雪！"));
            }

        }
    }

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

    public static boolean isPlayerPremium(MinecraftServer server, UUID uuid, String playerName) throws AuthenticationUnavailableException {
        GameProfile gameProfile = new GameProfile(uuid, playerName);
        MinecraftSessionService sessionService = server.getSessionService();
        return sessionService.hasJoinedServer(gameProfile, playerName, null) != null;
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
