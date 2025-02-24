package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class Neuron extends Item {
    public Neuron() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.COMMON));
    }
}
