package com.csdy.jzyy.item.food;


import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class Mtf extends ItemGenericFood {

    public Mtf() {
        super(2, 2F, false, false, true, 64);
    }

    @Override
    public void onFoodEaten(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 10, 0));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        super.finishUsingItem(stack, worldIn, livingEntity);
        var player = (Player) livingEntity;
        if (player.level.isClientSide) player.displayClientMessage(Component.literal("代码能力+1！"), false);
        return stack;
    }
}
