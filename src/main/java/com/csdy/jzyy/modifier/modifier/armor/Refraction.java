package com.csdy.jzyy.modifier.modifier.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
///ug的折射，护甲词条
public class Refraction extends Modifier implements ModifyDamageModifierHook, OnAttackedModifierHook {

    private static final ThreadLocal<Boolean> IS_HANDLING_ATTACK = ThreadLocal.withInitial(() -> false);

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {

        if (IS_HANDLING_ATTACK.get()) {
            return;
        }

        if (!(context.getEntity() instanceof Player player)) return;

        Entity attacker = damageSource.getEntity();
        if (attacker != null && !player.getCooldowns().isOnCooldown(tool.getItem()) && !attacker.equals(player)) {
            try {
                IS_HANDLING_ATTACK.set(true);

                attacker.hurt(player.damageSources().playerAttack(player), amount);

                player.getCooldowns().addCooldown(tool.getItem(), 20);
            } finally {
                IS_HANDLING_ATTACK.set(false);
            }
        }
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (context.getEntity() == null) return amount;
        return (float) (amount * (1 - (0.03 + 0.08 * entry.getLevel())));
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }




}
