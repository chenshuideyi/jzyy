package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RefineMerorArmor {

    public static class lowinjurydisregard extends NoLevelsModifier implements ModifyDamageModifierHook {

        
        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
        }

        @Override
        public float modifyDamageTaken(@NotNull IToolStackView iToolStackView, @NotNull ModifierEntry modifierEntry, @NotNull EquipmentContext equipmentContext, @NotNull EquipmentSlot equipmentSlot, @NotNull DamageSource damageSource, float v, boolean b) {
        if (v < 7) {
            return 0;
        }
        return v;
         }
    }
}