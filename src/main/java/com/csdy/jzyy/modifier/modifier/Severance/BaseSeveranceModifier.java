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

import static com.csdy.jzyy.method.LivingEntityUtil.reflectionPenetratingDamage;

@Getter
public class BaseSeveranceModifier extends NoLevelsModifier implements MeleeHitModifierHook {

    private final float value;

    public BaseSeveranceModifier(float value) {
        this.value = value;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            reflectionPenetratingDamage(target,player,damageDealt * this.value);
            System.out.println("切断！");
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

}
