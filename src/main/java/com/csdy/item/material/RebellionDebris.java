package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class RebellionDebris extends Item {
    public  RebellionDebris() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.EPIC));
    }
}
