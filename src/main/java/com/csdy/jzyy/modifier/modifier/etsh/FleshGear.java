package com.csdy.jzyy.modifier.modifier.etsh;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.modifier.modifier.etsh.GpuUtil.getTotalVideoMemory;

public class FleshGear extends NoLevelsModifier  implements ModifyDamageModifierHook, OnAttackedModifierHook {

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;
        player.heal(player.getHealth() * getTotalVideoMemory());
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player)) return amount;
        return amount / getTotalVideoMemory();
    }

}
