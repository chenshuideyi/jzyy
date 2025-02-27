package com.csdy.item.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Poop extends ItemGenericFood {
    String firstshit = "first_shit";
    public Poop() {
        super(15, 15F, false, false, true, 64);
    }

    @Override
    public void onFoodEaten(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 300, 0));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        super.finishUsingItem(stack, worldIn, livingEntity);
        Player player = (Player) livingEntity;
        //Warden.addWarden(player);
        CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (!tag.getBoolean(firstshit)){
        player.displayClientMessage(Component.literal("你居然连这个都吃!但是不会给你成就的"), false);
        player.getPersistentData().getBoolean("first_shit");
        tag.putBoolean(firstshit, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        }
        return stack;
    }
}

