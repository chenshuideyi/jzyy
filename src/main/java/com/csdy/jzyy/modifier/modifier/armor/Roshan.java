package com.csdy.jzyy.modifier.modifier.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Roshan extends NoLevelsModifier implements ModifyDamageModifierHook {

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        var entity = context.getEntity();
        if (entity == null) return amount;

        float currentHealth = entity.getHealth();

        // 血量越高，减伤越强（每 10 点生命值减少 1% 伤害，最多 50% 减伤）
        float damageReduction = Math.min(currentHealth / 10f * 0.01f, 0.5f);

        return amount * (1.0f - damageReduction);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
    }

}
