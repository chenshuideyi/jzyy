package com.csdy.jzyy.item.food;


import com.csdy.jzyy.shader.BlackFogEffect;
import com.csdy.jzyy.shader.BloodSkyEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.csdy.jzyy.entity.boss.bartest.GoldMcCreeBartest.testX;
import static com.csdy.jzyy.entity.boss.bartest.GoldMcCreeBartest.testY;


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
        else {
            testX += 10;
            testY += 10;
//            BlackFogEffect.SetEnableTo((ServerPlayer) player, true);
        }
        return stack;
    }
}
