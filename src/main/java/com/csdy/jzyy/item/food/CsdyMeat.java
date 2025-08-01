package com.csdy.jzyy.item.food;

import com.csdy.jzyy.effect.JzyyEffectRegister;
import com.csdy.jzyy.modifier.util.font.RainbowFont;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CsdyMeat extends ItemGenericFood {

    public CsdyMeat() {
        super(16384, 16384, false, true, true, 1);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        super.finishUsingItem(stack, worldIn, livingEntity);
        var player = (Player) livingEntity;
        player.addEffect(new MobEffectInstance(JzyyEffectRegister.CSDY.get(),221 * 20,0));
        if (player.level.isClientSide) {
            player.playSound(JzyySoundsRegister.GIRL_A.get());
        }
        return stack;
    }
}
