package com.csdy.jzyy.modifier.util;

import slimeknights.tconstruct.library.tools.stat.*;

import java.util.Collection;

public class ToolStatsUtil {
    public static void multiplyAllToolStats(ModifierStatsBuilder builder, double multiplier) {

        Collection<IToolStat<?>> allStats = ToolStats.getAllStats();

        for (IToolStat<?> stat : allStats) {
            if (stat instanceof INumericToolStat<?> numericStat) {
                numericStat.multiply(builder, multiplier);
            }
        }
    }
}
