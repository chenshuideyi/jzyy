package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class VirtualCaveCrystalArmor {

        public static class guixu extends Modifier implements OnAttackedModifierHook {

            private static final float INVULNERABILITY_INCREASE_PER_LEVEL = 0.2f;

            @Override
            protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
                super.registerHooks(hookBuilder);

                hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
            }

            @Override
            public void onAttacked(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentContext context,
                                   @NotNull EquipmentSlot slot, @NotNull DamageSource source, float originalDamage, boolean isDirectDamage) {

                if (context.getLevel().isClientSide()) {
                    return;
                }
                LivingEntity wearer = context.getEntity();
                int level = modifier.getLevel();
                float increaseRatio = Math.min(5.0f, level * INVULNERABILITY_INCREASE_PER_LEVEL);

                int originalInvulnTime = wearer.invulnerableTime;
                int originalInvulnDuration = wearer.invulnerableDuration;
                wearer.invulnerableTime = (int) (originalInvulnTime * (1 + increaseRatio));
                wearer.invulnerableDuration = (int) (originalInvulnDuration * (1 + increaseRatio));
            }
        }
}
