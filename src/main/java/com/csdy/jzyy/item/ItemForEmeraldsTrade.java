package com.csdy.jzyy.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ItemForEmeraldsTrade implements VillagerTrades.ItemListing {
    private final ItemStack sellItem;
    private final int emeraldCost;
    private final int count;
    private final int maxUses;
    private final int xpValue;

    public ItemForEmeraldsTrade(ItemStack sellItem, int emeraldCost, int count, int maxUses, int xpValue) {
        this.sellItem = sellItem;
        this.emeraldCost = emeraldCost;
        this.count = count;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
    }

    @Nullable
    @Override
    public @org.jetbrains.annotations.Nullable MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource randomSource) {
        ItemStack itemToSell = new ItemStack(this.sellItem.getItem(), this.count);
        return new MerchantOffer(
                new ItemStack(Items.EMERALD, this.emeraldCost),
                itemToSell,
                this.maxUses,
                this.xpValue,
                0.05F
        );
    }
}
