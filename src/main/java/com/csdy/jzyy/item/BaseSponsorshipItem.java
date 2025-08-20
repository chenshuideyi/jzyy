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

public class BaseSponsorshipItem extends Item {

    private final String line;
    private final ChatFormatting chatFormatting;

    public BaseSponsorshipItem(Rarity rarity,String line,ChatFormatting chatFormatting) {
        super((new Item.Properties()).stacksTo(64).rarity(rarity));
        this.line = line;
        this.chatFormatting = chatFormatting;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable(this.line).withStyle(this.chatFormatting).withStyle(ChatFormatting.ITALIC));
    }

}
