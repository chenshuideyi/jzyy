package com.csdy.jzyy.modifier.modifier.valstrax;

import com.csdy.jzyy.modifier.register.JzyyModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import static slimeknights.tconstruct.library.tools.stat.ToolStats.ARMOR;

public class GoldFingerArmor extends JzyyModifier {

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        volatileData.addSlots(SlotType.UPGRADE, 1);
        volatileData.addSlots(SlotType.ABILITY, 4);
        volatileData.addSlots(SlotType.DEFENSE, 1);
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder){
        ARMOR.add(builder, -48);
    }

}
