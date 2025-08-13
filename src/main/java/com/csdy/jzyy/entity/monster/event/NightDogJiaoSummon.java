package com.csdy.jzyy.entity.monster.event;


import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NightDogJiaoSummon {
    static Random random = new Random();

    private static final int COUNT = 5;
    private static final int MIN_DISTANCE = 6;  // 最小距离(格)
    private static final int MAX_DISTANCE = 12; // 最大距离(格)
    private static final int CHECK_INTERVAL = 200; // 10秒 = 200 ticks
    private static int timer = 0;


    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {

        if (++timer < CHECK_INTERVAL) return;
        timer = 0;

        if (event.phase != TickEvent.Phase.END ||
                event.level.isClientSide ||
                !(event.level.getDayTime() % 24000 > 12000)) return; // 只在夜晚检测


        for (Player player : event.level.players()) {
            // 在生成前检查玩家状态
            if (player.isSpectator() ||
                    player.isCreative() ||
                    player.isUnderWater()) {
                return;
            }

            String s = "no_da_gou_jiao";
            if (player.getPersistentData().getBoolean(s)){
                return;
            }
            if (player.getPersistentData().getLong("LastDogJiaoSpawn") + 24000 > event.level.getGameTime()) {
                return;
            }

            player.getPersistentData().putLong("LastDogJiaoSpawn", event.level.getGameTime());

            trySpawnZombiesNearPlayer((ServerLevel) event.level, player);

            if (!player.level().isClientSide()) {
                JzyySyncing.CHANNEL.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new PlaySoundPacket(player.getEyePosition(), JzyySoundsRegister.DOG_JIAO_SUMMON.get().getLocation(),1,1)
                );
            }
        }
    }

    public static void trySpawnZombiesNearPlayer(ServerLevel level, Player player) {

        for (int i = 0; i < COUNT; i++) {
            // 计算随机位置
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = MIN_DISTANCE + random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;
            double y = player.getY() + random.nextInt(3) - 1;

            // 验证位置是否可生成
            if (level.noCollision(JzyyEntityRegister.DOG_JIAO.get().getAABB(x, y, z))) {
                DogJiao dogJiao = JzyyEntityRegister.DOG_JIAO.get().create(level);
                if (dogJiao != null) {
                    dogJiao.setPos(x, y, z);
                    level.addFreshEntity(dogJiao);

                    // 可选：给僵尸添加特效
                    dogJiao.addEffect(new MobEffectInstance(
                            MobEffects.GLOWING, 200, 0, false, false
                    ));
                }
            }
        }
    }

}
