package com.csdy.jzyy.modifier.modifier.Severance;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.*;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.modifierSeverance;

public class BaseCuttingModifier extends NoLevelsModifier implements MeleeHitModifierHook {

    private final float value;
    private final float baseDamage;

    public BaseCuttingModifier(float value,float baseDamage) {
        this.value = value;
        this.baseDamage = baseDamage;
    }


    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            if (target.getHealth() <= 0) return knockback;
            if (isFromDummmmmmyMod(target)) return knockback;
            if (isDefender(target)) return knockback;
            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            modifierCutting(target,player,toolDamage,this.value + this.baseDamage);
        }
        return knockback;
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            if (target.getHealth() <= 0) return;
            if (isFromDummmmmmyMod(target)) return;
            if (isDefender(target)) return;
            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            modifierCutting(target,player,toolDamage,this.value + this.baseDamage);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }



}
