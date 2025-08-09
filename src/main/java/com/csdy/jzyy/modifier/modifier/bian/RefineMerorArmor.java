package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RefineMerorArmor {

    public static class lowinjurydisregard extends Modifier implements DamageBlockModifierHook {

        @Override
        public boolean isDamageBlocked(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, @NotNull EquipmentContext context,
                                       @NotNull EquipmentSlot slot, @NotNull DamageSource source, float damage) {
            return damage <= 7;
        }

        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
        }

    }
}
