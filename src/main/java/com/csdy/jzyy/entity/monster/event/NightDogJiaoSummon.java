package com.csdy.jzyy.entity.monster.event;


import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.boss.entity.DogJiaoJiaoJiao;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
                !(event.level.getDayTime() % 24000 > 12000)) return;

        for (Player player : event.level.players()) {
            if (player.isSpectator() || player.isCreative() || player.isUnderWater()) {
                continue;
            }

            // 检查是否已经召唤过大狗叫叫叫
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag playerTag = persistentData.getCompound(Player.PERSISTED_NBT_TAG);

            if (playerTag.getBoolean("HasSpawnedDogJiaoJiaoJiao")) {
                continue; // 已经召唤过，不再处理
            }

            if (player.getPersistentData().getLong("LastDogJiaoSpawn") + 24000 > event.level.getGameTime()) {
                continue;
            }

            // 获取召唤计数
            int summonCount = playerTag.getInt("DogJiaoSummonCount");

            if (summonCount == 9) { // 第十次召唤（0-9）
                // 启动预警线程
                new Thread(() -> {
                    try {
                        // 发送预警消息
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(Component.literal("你感到一个巨大的存在正在靠近你"));
                        }

                        // 等待30秒
                        Thread.sleep(30000);

                        // 在主线程执行生成逻辑
                        Minecraft.getInstance().execute(() -> {
                            if (!player.level().isClientSide()) {
                                trySpawnDogJiaoJiaoJiao((ServerLevel) event.level, player);

                                // 标记已召唤过大狗叫叫叫
                                playerTag.putBoolean("HasSpawnedDogJiaoJiaoJiao", true);
                                persistentData.put(Player.PERSISTED_NBT_TAG, playerTag);
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                // 增加计数并设置冷却
                playerTag.putInt("DogJiaoSummonCount", summonCount + 1);
                persistentData.put(Player.PERSISTED_NBT_TAG, playerTag);
                player.getPersistentData().putLong("LastDogJiaoSpawn", event.level.getGameTime());

            } else if (summonCount < 9) {
                // 普通召唤僵尸
                trySpawnZombiesNearPlayer((ServerLevel) event.level, player, COUNT);

                // 增加召唤计数
                playerTag.putInt("DogJiaoSummonCount", summonCount + 1);
                persistentData.put(Player.PERSISTED_NBT_TAG, playerTag);
                player.getPersistentData().putLong("LastDogJiaoSpawn", event.level.getGameTime());

                if (!player.level().isClientSide()) {
                    JzyySyncing.CHANNEL.send(
                            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                            new PlaySoundPacket(player.getEyePosition(),
                                    JzyySoundsRegister.DOG_JIAO_SUMMON.get().getLocation(), 1, 1)
                    );
                }
            }
        }
    }

    // 简化的生成大狗叫叫叫方法
    private static void trySpawnDogJiaoJiaoJiao(ServerLevel level, Player player) {
        // 在玩家附近生成
        double angle = level.getRandom().nextDouble() * Math.PI * 2;
        double distance = 10 + level.getRandom().nextDouble() * 10;

        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        double y = player.getY();

        BlockPos spawnPos = new BlockPos((int)x, (int)y, (int)z);

        // 创建大狗叫叫叫
        EntityType<?> dogJiaoType = JzyyEntityRegister.DOG_JIAO_JIAO_JIAO.get();
        DogJiaoJiaoJiao dogJiao = (DogJiaoJiaoJiao) dogJiaoType.create(level);

        if (dogJiao != null) {
            dogJiao.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            dogJiao.setTarget(player);
            level.addFreshEntity(dogJiao);

            // 播放特殊音效
            JzyySyncing.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new PlaySoundPacket(player.getEyePosition(),
                            JzyySoundsRegister.SCREAM2.get().getLocation(), 2, 1)
            );

        }
    }

    public static void trySpawnZombiesNearPlayer(ServerLevel level, Entity player,int count) {

        for (int i = 0; i < count; i++) {
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
