package com.csdy.jzyy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
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

public class Silence extends Item {
    public Silence() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.RARE));
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isInPlayerInventoryGUI()) {
            return Component.translatable("item.jzyy.silence.inventory_name")
                    .withStyle(ChatFormatting.GOLD);
        }
        return Component.translatable("item.jzyy.silence.default_name");
    }

    @OnlyIn(Dist.CLIENT)
    private boolean isInPlayerInventoryGUI() {
        Minecraft mc = Minecraft.getInstance();
        return mc.screen instanceof InventoryScreen
//                && !(mc.screen instanceof CreativeModeInventoryScreen)
                && !(mc.screen instanceof ContainerScreen); // 排除箱子等容器
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.silence_line1").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
    }
}
