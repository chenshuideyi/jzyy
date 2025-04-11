package com.csdy.jzyy.item.disc;


import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.world.item.Rarity;

public class DiscAloneInTheDarkItem extends ModDiscItem {
    public DiscAloneInTheDarkItem() {
        super(1, JzyySoundsRegister.CUMULONIMBUS, new Properties().fireResistant().stacksTo(1).rarity(Rarity.COMMON), 240*20+20*46);
    }

}
