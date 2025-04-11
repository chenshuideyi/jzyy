package com.csdy.jzyy.modifier.modifier;

import com.csdy.jzyy.modifier.modifier.Severance.AbsoluteSeverance;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Cosmos extends AbsoluteSeverance implements MeleeHitModifierHook {
    public Cosmos(float value) {
        super(value);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        if (context.getLivingTarget() != null) {
            context.getLivingTarget().setHealth(0);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
