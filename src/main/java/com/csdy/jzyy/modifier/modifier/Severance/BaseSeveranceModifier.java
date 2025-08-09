package com.csdy.jzyy.modifier.modifier.Severance;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

@Getter
public class BaseSeveranceModifier extends NoLevelsModifier implements MeleeHitModifierHook {
    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    private final float value;

    public BaseSeveranceModifier(float value) {
        this.value = value;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            if (target.getHealth() <= 0) return;
            if (isFromDummmmmmyMod(target)) return;
            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            modifierSeverance(target,player,toolDamage,this.value);
        }
    }

//    @Override
//    public float beforeMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
//        LivingEntity target = context.getLivingTarget();
//        Player player = context.getPlayerAttacker();
//        if (target != null && player != null) {
//            if (target.getHealth() <= 0) return knockback;
//            if (isFromDummmmmmyMod(target)) return knockback;
//            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
//            modifierSeverance(target,player,toolDamage,this.value);
//        }
//        return knockback;
//    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

}
