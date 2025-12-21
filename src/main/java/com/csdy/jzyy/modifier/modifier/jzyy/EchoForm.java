package com.csdy.jzyy.modifier.modifier.jzyy;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EchoForm extends NoLevelsModifier implements MeleeDamageModifierHook {

    @Override
    public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {

        if (!(context.getAttacker() instanceof ServerPlayer player)) {
            return damage;
        }

        for (ModifierEntry modifier : tool.getModifierList()) {
            if (modifier.getModifier() == entry.getModifier()) {
                continue;
            }

            MeleeDamageModifierHook damageHook = modifier.getHook(ModifierHooks.MELEE_DAMAGE);
            if (damageHook != null && damageHook != this) {
                damage = damageHook.getMeleeDamage(tool, modifier, context, baseDamage, damage);
            }
        }


        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }
}

