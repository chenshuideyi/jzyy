package com.csdy.jzyy.entity.monster.event;


import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HJMSummon {
    static Random random = new Random();

    private static final int CHECK_INTERVAL = 200; // 10秒 = 200 ticks
    private static int timer = 0;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (++timer < CHECK_INTERVAL) return;
        timer = 0;
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;


        for (Player player : event.level.players()) {
            if (player.isSpectator() ||
                    player.isCreative() ||
                    player.isUnderWater()) {
                return;
            }
            List<HJMEntity> list = event.level.getEntitiesOfClass(HJMEntity.class,player.getBoundingBox().inflate(200));
            if (list.size()>3)return;

            int a = random.nextInt(40);
            if (a==1) {
                trySpawnEntityNearPlayer(JzyyEntityRegister.HJM.get(),(ServerLevel) event.level, player,1,18,30,null,0,0);
                player.displayClientMessage(Component.literal("感觉到了[哈！]的气息…"), true);
            }
        }
    }

    public static void trySpawnEntityNearPlayer(EntityType type, ServerLevel level, Player player, int COUNT, int MIN_DISTANCE, int MAX_DISTANCE, MobEffect effect,int duration,int amplifier) {
        for (int i = 0; i < COUNT; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = MIN_DISTANCE + random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;
            double y = player.getY() + random.nextInt(3) - 1;
            if (level.noCollision(type.getAABB(x, y, z)) && type.create(level) instanceof LivingEntity living) {
                living.setPos(x, y, z);
                level.addFreshEntity(living);
                if (effect == null || duration < 0 || amplifier < 0) return;
                living.addEffect(new MobEffectInstance(
                        effect, duration, amplifier, false, false
                ));
            }
        }
    }
}
