package com.csdy.jzyy.item;

import com.csdy.jzyy.modifier.util.font.RainbowFont;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RainbowMaterial extends Item {

    public RainbowMaterial() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.rainbow_material.line1").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC));
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @javax.annotation.Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return RainbowFont.getFont();
            }
        });
    }
}
