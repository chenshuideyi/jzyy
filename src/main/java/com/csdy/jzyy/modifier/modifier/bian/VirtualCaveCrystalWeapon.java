package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class VirtualCaveCrystalWeapon {

    public static class shattertheemptiness extends Modifier implements MeleeDamageModifierHook {


        private static final float INVULNERABILITY_REDUCTION_PER_LEVEL = 0.2f;

        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
            super.registerHooks(hookBuilder);
        }

        @Override
        public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier,
                                    @NotNull ToolAttackContext context, float baseDamage, float damage) {

            LivingEntity target = context.getTarget() instanceof LivingEntity ?
                    (LivingEntity) context.getTarget() : null;

            if (target != null && !target.isDeadOrDying() && !context.getLevel().isClientSide()) {
                int level = modifier.getLevel();
                float reduction = Math.min(5.0f, level * INVULNERABILITY_REDUCTION_PER_LEVEL);

                int originalInvulnTime = target.invulnerableTime;
                int newInvulnTime = (int) (originalInvulnTime * (1 - reduction));
                target.invulnerableTime = newInvulnTime;
                target.invulnerableDuration = newInvulnTime;
            }

            return damage;
        }
    }
}