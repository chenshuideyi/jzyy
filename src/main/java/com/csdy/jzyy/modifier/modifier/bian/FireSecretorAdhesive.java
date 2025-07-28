package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class FireSecretorAdhesive {

    public static class fireabsorption extends Modifier implements OnAttackedModifierHook, ModifyDamageModifierHook {

        private static final float DAMAGE_REDUCTION_PER_LEVEL = 0.16f;



        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            super.registerHooks(hookBuilder);
        }


        @Override
        public float modifyDamageTaken(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentContext context, @NotNull EquipmentSlot slot, @NotNull DamageSource source, float damage, boolean isDirectDamage) {

            if (isFIREDamage(source)) {
                int level = modifier.getLevel();
                float reduction = level * DAMAGE_REDUCTION_PER_LEVEL;
                return damage * (1 - reduction);
            }
            return damage;
        }

        @Override
        public void onAttacked(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentContext context, @NotNull EquipmentSlot slot, @NotNull DamageSource source, float originalDamage, boolean isDirectDamage) {

            if (isFIREDamage(source)) {
                int level = modifier.getLevel();
                float reduction = level * DAMAGE_REDUCTION_PER_LEVEL;
                float absorbedDamage = originalDamage * reduction;

                LivingEntity entity = context.getEntity();
                entity.heal(absorbedDamage);
            }
        }


        private boolean isFIREDamage(DamageSource source) {
            Holder<DamageType> damageType = source.typeHolder();
            return damageType.is(DamageTypeTags.IS_FIRE);
        }
    }
}




