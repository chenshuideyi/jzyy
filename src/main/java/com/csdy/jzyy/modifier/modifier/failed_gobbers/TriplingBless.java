package com.csdy.jzyy.modifier.modifier.failed_gobbers;

import com.csdy.jzyy.modifier.register.JzyyModifier;
import com.csdy.jzyy.modifier.util.CsdyModifierUtil;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TriplingBless extends JzyyModifier {

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        CsdyModifierUtil.multiplyAllToolStats(builder,0.0001f);
    }
}
