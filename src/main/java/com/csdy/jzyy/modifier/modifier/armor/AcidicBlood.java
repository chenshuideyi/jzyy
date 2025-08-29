package com.csdy.jzyy.modifier.modifier.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.modifierAbsoluteSeverance;

public class AcidicBlood extends NoLevelsModifier implements OnAttackedModifierHook {

    private static final ThreadLocal<Boolean> IS_HANDLING_ATTACK = ThreadLocal.withInitial(() -> false);

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {

        if (IS_HANDLING_ATTACK.get()) {
            return;
        }

        if (!(context.getEntity() instanceof Player player)) return;

        Entity attacker = damageSource.getEntity();
        if (attacker != null && !player.getCooldowns().isOnCooldown(tool.getItem()) && !attacker.equals(player) && attacker instanceof LivingEntity living) {
            try {
                IS_HANDLING_ATTACK.set(true);
                modifierAbsoluteSeverance(living,player,amount,1);
            } finally {
                IS_HANDLING_ATTACK.set(false);
            }
        }
    }



    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }




}
