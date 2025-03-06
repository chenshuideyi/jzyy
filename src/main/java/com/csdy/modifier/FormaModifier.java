package com.csdy.modifier;

import com.csdy.DiademaSlots;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.Random;

public class FormaModifier extends Modifier implements VolatileDataModifierHook {
    static final SlotType[] Slots = new SlotType[]{
            SlotType.UPGRADE,SlotType.UPGRADE,SlotType.UPGRADE,
            SlotType.DEFENSE,SlotType.DEFENSE,SlotType.DEFENSE,
            SlotType.ABILITY,
            SlotType.SOUL,
    };
//    Random random = new Random();

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT nbt) {
        int level =modifier.getLevel();
        for(int i = 0; i < Math.min(level,Slots.length); i++){
//            int n = random.nextInt(Slots.length);
            nbt.addSlots(Slots[i],1);
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }
}
