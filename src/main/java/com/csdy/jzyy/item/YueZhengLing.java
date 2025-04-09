package com.csdy.jzyy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YueZhengLing extends Item {
    public YueZhengLing() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.COMMON));
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.yue_zheng_ling.line1").withStyle(ChatFormatting.STRIKETHROUGH).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("item.jzyy.yue_zheng_ling.line2").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
    }

}
