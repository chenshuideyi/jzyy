package com.csdy.jzyy.modifier.modifier.ice_and_fire.armor;

import com.csdy.jzyy.effect.JzyyEffectRegister;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class FlashDragonArmor extends NoLevelsModifier implements ModifyDamageModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        var holder = context.getEntity();
        if (damageSource.is(DamageTypes.LIGHTNING_BOLT)
            || damageSource.getMsgId().contains("lightning_bolt")
            || damageSource.getMsgId().contains("f_268450_")
        ) {
            holder.addEffect(new MobEffectInstance(JzyyEffectRegister.OVERCHARGE_ARMOR.get(),600,0));
            return 0; // 完全免疫雷电伤害
        }
        return amount;
    }

}
