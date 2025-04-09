package com.csdy.jzyy.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class OverflowedMemory extends Item {
    public OverflowedMemory() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.COMMON));
    }

}
