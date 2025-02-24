package com.csdy.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class NeuralSensors extends Item {
    public NeuralSensors() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.COMMON));
    }
}
