package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BulgeRod {

    public static class wave extends Modifier implements MeleeDamageModifierHook {

        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        }

        @Override
        public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifierEntry, @NotNull ToolAttackContext context, float baseDamage, float damage) {
            LivingEntity target = context.getLivingTarget();
            if (target == null || !target.isAlive()) {
                return damage;
            }


            int level = modifierEntry.getLevel();
            DamageSource suffocationSource = target.damageSources().inWall();
            float suffocationBase = 20.0f * level;
            float magicBase = 10.0f * level;
            if (!target.isInvulnerableTo(suffocationSource)) {
                target.hurt(suffocationSource, suffocationBase);
            } else {
                DamageSource magicSource = target.damageSources().magic();
                target.hurt(magicSource, magicBase);
            }

            return damage;
        }
    }
}
