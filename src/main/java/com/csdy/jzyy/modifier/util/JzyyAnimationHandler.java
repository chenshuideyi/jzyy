package com.csdy.jzyy.modifier.util;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class JzyyAnimationHandler {

    private static final Map<ItemEntity, Integer> ANIMATION_ITEMS = new WeakHashMap<>();

    public static void addCustomTotemAnimation(ItemEntity item, Player player) {
        ANIMATION_ITEMS.put(item, 0);
        // 注册客户端动画效果
        if (player.level().isClientSide) {
            // 这里可以添加客户端特效
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<Map.Entry<ItemEntity, Integer>> iterator = ANIMATION_ITEMS.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemEntity, Integer> entry = iterator.next();
                ItemEntity item = entry.getKey();
                int tick = entry.getValue();

                if (!item.isAlive() || tick > 40) { // 40 ticks = 2秒动画
                    item.discard();
                    iterator.remove();
                    continue;
                }

                // 更新动画位置
                double progress = tick / 40.0;
                double yOffset = Math.sin(progress * Math.PI) * 2.0; // 上下浮动
                double scale = 1.0 + progress * 0.5; // 逐渐变大

                item.setPos(
                        item.getX(),
                        item.getY() + yOffset * 0.1,
                        item.getZ()
                );

                // 设置旋转
                item.setYRot(tick * 10);

                // 存储动画进度
                entry.setValue(tick + 1);
            }
        }
    }








}
