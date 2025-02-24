package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class Forma extends Item {
    public Forma() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.UNCOMMON));
    }
}
