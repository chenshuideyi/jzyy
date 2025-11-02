package com.csdy.jzyy.modifier.modifier.bian;

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


public class LifeAndArmor extends NoLevelsModifier implements MeleeHitModifierHook {
    private final float absoluteSeveranceValue = 0.7F;
    private final float absoluteSeveranceBaseDamage = 0;

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damage, float baseKnockback, float knockback) {

        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        Player player = context.getPlayerAttacker();


        if (target != null && attacker != null && player != null && target.getHealth() > 0) {

            if (isFromDummmmmmyMod(target) || isDefender(target) || isFromIceAndFire(target)) {
                return knockback;
            }


            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);


            float health = attacker.getHealth();
            float armorValue = attacker.getArmorValue();


            float difference = health - armorValue;
            float extraDamageFactor = difference * difference ;


            float finalSeveranceValue = absoluteSeveranceValue + extraDamageFactor;


            modifierAbsoluteSeverance(target, player, toolDamage, finalSeveranceValue + absoluteSeveranceBaseDamage);
        }

        return knockback;
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
