package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class BrokenSacredRelic extends Item {
    public BrokenSacredRelic() {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON));
    }
}
