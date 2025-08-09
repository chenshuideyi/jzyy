package com.csdy.jzyy.item.food;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoldenStrawberry extends ItemGenericFood {

    public GoldenStrawberry() {
        super(6, 2, false, true, true, 64);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.golden_strawberry.line1").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
    }
}

