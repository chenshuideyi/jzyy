package com.csdy.item;

import com.csdy.item.food.ItemGenericFood;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderDragonHeart extends ItemGenericFood {
    public EnderDragonHeart() {
        super(80, 80F, false, true, true, 1);
    }

}
