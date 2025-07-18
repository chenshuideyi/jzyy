package com.csdy.jzyy.modifier.modifier.Severance;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionPenetratingDamage;
import static slimeknights.tconstruct.library.tools.stat.ToolStats.ATTACK_DAMAGE;

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
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            float severanceDamage = damage + tool.getMultiplier(ATTACK_DAMAGE) * this.value;;
            reflectionPenetratingDamage(target,player,severanceDamage);
        }
        return knockback;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

}
