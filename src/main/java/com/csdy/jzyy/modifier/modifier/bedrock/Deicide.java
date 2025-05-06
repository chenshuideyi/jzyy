package com.csdy.jzyy.modifier.modifier.bedrock;

import net.minecraft.server.level.ServerPlayer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionSetHealth;

public class Deicide extends NoLevelsModifier implements MeleeDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        var target = context.getLivingTarget();
        var holder = context.getPlayerAttacker();
        if (!(target instanceof ServerPlayer player)) return damage;
        if(player.isCreative()) reflectionSetHealth(player,holder,0);
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }
}
