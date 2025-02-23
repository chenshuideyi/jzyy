package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class OriginalCatalyst extends Item {
    public OriginalCatalyst() {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC));
    }
}
