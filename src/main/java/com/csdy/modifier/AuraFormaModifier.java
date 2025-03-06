package com.csdy.modifier;

import com.csdy.DiademaSlots;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

public class AuraFormaModifier extends NoLevelsModifier implements VolatileDataModifierHook {
    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT nbt) {
        nbt.addSlots(DiademaSlots.DIADEMA,1);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }
}
