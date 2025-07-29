package com.csdy.jzyy.modifier.modifier.hallowed_bar;

import com.csdy.jzyy.effect.JzyyEffectRegister;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class HolyProtection extends NoLevelsModifier  implements DamageBlockModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        if (!context.getEntity().hasEffect(JzyyEffectRegister.HOLY_PROTECTION_COOLDOWN.get())){
            context.getEntity().addEffect(new MobEffectInstance(JzyyEffectRegister.HOLY_PROTECTION_COOLDOWN.get(),600,0));
            return true;
        }
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
    }

}
