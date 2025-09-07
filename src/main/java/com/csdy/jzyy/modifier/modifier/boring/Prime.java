package com.csdy.jzyy.modifier.modifier.boring;

import com.csdy.jzyy.modifier.util.CsdyModifierUtil;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

public class Prime extends NoLevelsModifier implements ToolStatsModifierHook, VolatileDataModifierHook {

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry entry, ToolDataNBT volatileData) {
        volatileData.addSlots(SlotType.DEFENSE, 2);
        volatileData.addSlots(SlotType.UPGRADE, 2);
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        CsdyModifierUtil.multiplyAllToolStats(builder, 1.5f);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }

}