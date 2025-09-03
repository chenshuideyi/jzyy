package com.csdy.jzyy.modifier.modifier.soul;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class UnDeadSoul extends NoLevelsModifier implements InventoryTickModifierHook {

    private static final int NIGHT_VISION_DURATION = 300; // 15秒 * 20 ticks
    private static final int CHECK_INTERVAL = 20; // 每秒钟检查一次

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (world.isClientSide) return; // 只在服务端执行

        // 每秒钟检查一次，避免每tick都执行
        if (world.getGameTime() % CHECK_INTERVAL != 0) return;

        if (isCorrectSlot) {
            // 给玩家夜视效果
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    NIGHT_VISION_DURATION,
                    0, // 等级0
                    false, // 环境效果
                    false, // 不显示粒子
                    true // 显示图标
            ));

            // 检查是否是白天并且玩家在户外
            if (isDaytime(world) && isExposedToSky(world, player)) {
                // 像亡灵生物一样燃烧
                burn(player);
            } else {
                // 如果不是白天或在室内，熄灭火焰
                player.clearFire();
            }
        }
    }

    // 检查是否是白天
    private boolean isDaytime(Level world) {
        long time = world.getDayTime() % 24000;
        return time > 0 && time < 12000; // 0-12000是白天
    }

    // 检查玩家是否暴露在天空下
    private boolean isExposedToSky(Level world, LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        return world.canSeeSky(pos) &&
                world.getBrightness(LightLayer.SKY, pos) > 0 &&
                !entity.isInWaterRainOrBubble() &&
                !entity.isInPowderSnow;
    }


    private void burn(LivingEntity entity) {
        if (!entity.isOnFire()) {
            entity.setSecondsOnFire(8); // 8秒火焰
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
