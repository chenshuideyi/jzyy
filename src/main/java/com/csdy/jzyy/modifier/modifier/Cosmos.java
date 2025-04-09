package com.csdy.jzyy.modifier.modifier;

import com.csdy.jzyy.modifier.modifier.Severance.AbsoluteSeverance;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Cosmos extends AbsoluteSeverance {
    public Cosmos(float value) {
        super(value);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        if (context.getLivingTarget() != null) {
            context.getLivingTarget().setHealth(0);
            super.afterMeleeHit(tool, entry, context, damageDealt);
        }
    }
}
