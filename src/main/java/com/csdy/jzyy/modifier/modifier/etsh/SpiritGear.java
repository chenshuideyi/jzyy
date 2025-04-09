package com.csdy.jzyy.modifier.modifier.etsh;

import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;


public class SpiritGear extends NoLevelsModifier implements ToolStatsModifierHook {

    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        ToolStats.DRAW_SPEED.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.MINING_SPEED.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.DURABILITY.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.ATTACK_SPEED.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.VELOCITY.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.ACCURACY.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.PROJECTILE_DAMAGE.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.ARMOR.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, 1 + (0.062134 * getLoadedModCount()));
    }

    public int getLoadedModCount() {
        return ModList.get().getMods().size();
    }
}
