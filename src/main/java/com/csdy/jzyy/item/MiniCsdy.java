package com.csdy.jzyy.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class MiniCsdy extends Item {

    private static final List<String> MESSAGES = Arrays.asList(
            "你好，我是Csdy,也是整合包的作者",
            "这里是我按照1.7的记忆捏出来的地方",
            "所以你可以遗忘掉玩过的所有1.20整合包的经验随便走走",
            "如果你因为古代模组的恶意受到创伤，可以来找我反馈",
            "现代游戏都对新手太友好了，正如现代mc整合包的任务都是告诉你怎么一步一步做一样",
            "对了，你知道吗：我正在练习自爆",
            "这就是老游戏！"
    );

    public MiniCsdy() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // 消耗物品
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            // 启动说话和自爆序列
            startCountdown(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void startCountdown(Player player) {
        for (int i = 0; i < MESSAGES.size(); i++) {
            final int index = i;
            final int delayTicks = 30 * i; // 每1.5秒一条消息

            // 创建延迟任务
            new Thread(() -> {
                try {
                    Thread.sleep(delayTicks * 50L); // 将ticks转换为毫秒 (1 tick = 50ms)

                    // 在主线程执行
                    player.level().getServer().execute(() -> {
                        if (player.isAlive() && !player.level().isClientSide) {
                            if (index < MESSAGES.size() - 1) {
                                player.sendSystemMessage(Component.literal(MESSAGES.get(index)));
                            } else {
                                player.sendSystemMessage(Component.literal(MESSAGES.get(index)));
                                explode(player);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void explode(Player player) {
        Level level = player.level();

        // 创建爆炸
        level.explode(
                player, // 爆炸源
                player.getX(), player.getY(), player.getZ(), // 爆炸位置
                3.0f, // 爆炸威力
                true, // 是否破坏方块
                Level.ExplosionInteraction.MOB // 爆炸类型
        );

        player.hurt(level.damageSources().explosion(player, player), 999.0f);

        // 播放爆炸声音
        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS,
                4.0F,
                (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F
        );
    }
}
