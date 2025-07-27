package com.csdy.jzyy.item.food;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PlayerMeat extends Item {
    public PlayerMeat() {
        super(new Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(3)
                        .saturationMod(1.6f)
                        .meat()
                        .alwaysEat()
                        .build())
                .stacksTo(16));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide && stack.hasTag()) {
            float healAmount = stack.getTag().getFloat("HealAmount");
            entity.heal(healAmount);
        }
        return super.finishUsingItem(stack, level, entity);
    }

}
