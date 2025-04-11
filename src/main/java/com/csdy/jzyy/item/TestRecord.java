package com.csdy.jzyy.item;

import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

public class TestRecord extends RecordItem {
    public TestRecord(int comparatorValue, SoundEvent soundEvent, Properties properties, int stack) {
        super(300, JzyySoundsRegister.CUMULONIMBUS, properties, 1);
    }


}
