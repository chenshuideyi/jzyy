package com.csdy.jzyy.modifier.modifier.primal_reversion;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TradeOff extends Modifier implements ToolStatsModifierHook {


    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        int rate = (int) Math.pow(2, entry.getLevel());
        ToolStats.MINING_SPEED.multiply(builder, rate);
        ToolStats.ATTACK_SPEED.multiply(builder, rate);
        ToolStats.ATTACK_DAMAGE.multiply(builder, rate);

        ToolStats.ARMOR.multiply(builder, rate);
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, rate);
        ToolStats.KNOCKBACK_RESISTANCE.multiply(builder, rate);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
    }
}
