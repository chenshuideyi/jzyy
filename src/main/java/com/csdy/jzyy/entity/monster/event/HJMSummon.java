package com.csdy.jzyy.entity.monster.event;


import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            int a = random.nextInt(19);
            if (a==1) {
                trySpawnHjmNearPlayer((ServerLevel) event.level, player,1,18,30);
            }
        }
    }

    public static void trySpawnHjmNearPlayer(ServerLevel level, Player player,int COUNT,int MIN_DISTANCE,int MAX_DISTANCE) {
        for (int i = 0; i < COUNT; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = MIN_DISTANCE + random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;
            double y = player.getY() + random.nextInt(3) - 1;
            if (level.noCollision(JzyyEntityRegister.HJM.get().getAABB(x, y, z))) {
                HJMEntity hjm = JzyyEntityRegister.HJM.get().create(level);
                if (hjm != null) {
                    hjm.setPos(x, y, z);
                    level.addFreshEntity(hjm);
                }
            }
        }
    }
}
