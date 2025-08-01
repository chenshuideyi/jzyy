package com.csdy.jzyy.entity.monster.event;


import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HJMSummon {
    static Random random = new Random();

    private static final int COUNT = 1;
    private static final int MIN_DISTANCE = 18;  // 最小距离(格)
    private static final int MAX_DISTANCE = 30; // 最大距离(格)
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
                trySpawnZombiesNearPlayer((ServerLevel) event.level, player);
            }
        }
    }

    public static void trySpawnZombiesNearPlayer(ServerLevel level, Player player) {
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
