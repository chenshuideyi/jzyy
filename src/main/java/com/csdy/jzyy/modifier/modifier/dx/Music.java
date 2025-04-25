package com.csdy.jzyy.modifier.modifier.dx;

import com.csdy.jzyy.DxSlots;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

public class Music extends NoLevelsModifier implements VolatileDataModifierHook {
    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT nbt) {
        nbt.addSlots(DxSlots.DX,1);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }
}
