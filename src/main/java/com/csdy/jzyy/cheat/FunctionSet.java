package com.csdy.jzyy.cheat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.csdy.jzyy.JzyyModMain.MODID;
import static com.csdy.jzyy.cheat.FieldList.ZFlyHeight;


@Mod.EventBusSubscriber(modid = MODID)
public class FunctionSet {
    public static double recordX;
    public static double recordY;
    public static double recordZ;



    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player && FieldList.NoFallDamage) {
            event.setDistance(0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerLand(LivingFallEvent event) {
        if (event.getEntity() instanceof Player && FieldList.NoFallDamage) {
            event.setDamageMultiplier(0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerFallWithThreshold(LivingFallEvent event) {
        if (event.getEntity() instanceof Player && FieldList.NoFallDamage) {
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
        if (!FieldList.HeadOutAccelerate || !mc.options.keyAttack.isDown() || !mc.player.getMainHandItem().isEmpty() || mc.player.swinging) {
            return;
        }
        Vec3 lookVec = mc.player.getLookAngle().normalize();
        double newX = mc.player.getX() + lookVec.x;
        double newZ = mc.player.getZ() + lookVec.z;
        mc.player.moveTo(newX, mc.player.getY(), newZ);
        mc.player.teleportTo(newX, mc.player.getY(), newZ);
        mc.player.setPos(newX, mc.player.getY(), newZ);
    }

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (FieldList.Fly) checkBooleanVariables(player);
        }
    }

    private static void checkBooleanVariables(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (FieldList.Fly) {
                mc.player.getAbilities().mayfly = true;
                if (mc.player.getAbilities().flying){
                    mc.player.getAbilities().flying = true;
                }
            }
            if (FieldList.Speed) {
                mc.player.getAbilities().setFlyingSpeed(0.3f);
            } else {
                mc.player.getAbilities().setFlyingSpeed(0.1f);
            }

            if (FieldList.ZFly) {
                if (mc.options.keyJump.isDown()) {
                    mc.player.teleportTo(mc.player.getX(), mc.player.getY() + ZFlyHeight / 20000, mc.player.getZ());
                    mc.player.setPos(mc.player.getX(), mc.player.getY() + ZFlyHeight / 20000, mc.player.getZ());
                    mc.player.moveTo(mc.player.getX(), mc.player.getY() + ZFlyHeight / 20000, mc.player.getZ());
                }
            }
            if (FieldList.JumpFar) {
                if (mc.options.keyJump.isDown()) {
                    Vec3 lookVec = mc.player.getLookAngle().normalize();
                    double newX = mc.player.getX() + lookVec.x;
                    double newZ = mc.player.getZ() + lookVec.z;
                    mc.player.moveTo(newX, mc.player.getY(), newZ);
                    mc.player.teleportTo(newX, mc.player.getY(), newZ);
                    mc.player.setPos(newX, mc.player.getY(), newZ);
                }
            }
        }
    }

    public static void RecordPlayerPosition() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            recordX = mc.player.getX();
            recordY = mc.player.getY();
            recordZ = mc.player.getZ();
        }
    }
}