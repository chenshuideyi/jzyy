package com.csdy.jzyy.item.food;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CsdyMeat extends ItemGenericFood {

    public CsdyMeat() {
        super(16384, 16384, false, true, true, 64);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        super.finishUsingItem(stack, worldIn, livingEntity);
        var player = (Player) livingEntity;
        if (player.hasEffect(JzyyEffectRegister.CSDY.get())) return stack;
        player.addEffect(new MobEffectInstance(JzyyEffectRegister.CSDY.get(),221 * 20,0));
        if (player.level.isClientSide) {
            player.playSound(JzyySoundsRegister.GIRL_A.get());
        }
        return stack;
    }
}
