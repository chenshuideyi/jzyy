package com.csdy.jzyy.modifier.modifier.bian;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


public class MerorArmor {

    public static class merortoughness extends Modifier implements ModifyDamageModifierHook {

        private static final float DAMAGE_REDUCTION_PER_LEVEL = 0.20f;


        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {

            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            super.registerHooks(hookBuilder);
        }



        @Override
        public float modifyDamageTaken(@NotNull IToolStackView iToolStackView, @NotNull ModifierEntry modifierEntry, @NotNull EquipmentContext equipmentContext, @NotNull EquipmentSlot equipmentSlot, @NotNull DamageSource damageSource, float v, boolean b) {
            if (!isbleedingDamage(damageSource)){
                int level = modifierEntry.getLevel();
                float reduction = level * DAMAGE_REDUCTION_PER_LEVEL;
                return v * (1 - reduction);
            }
            return v;
        }
        private boolean isbleedingDamage(DamageSource source) {
            Holder<DamageType> damageType = source.typeHolder();
            return damageType.is(JerotesDamageTypes.BLEEDING);
        }
    }
}