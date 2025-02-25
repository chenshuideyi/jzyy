package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class AuraForma extends Item {
    public AuraForma() {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON));
    }
}
