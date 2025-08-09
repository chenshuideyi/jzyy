package com.csdy.jzyy.modifier.modifier.dx;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.item.register.ItemRegister;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Random;

public class Refill extends NoLevelsModifier implements MeleeHitModifierHook, OnAttackedModifierHook {
    private static final Random random = new Random();

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;

        if (random.nextFloat() <= 0.02f) {
            ItemStack cap = new ItemStack(ItemRegister.MOZHUA_CAP.get());

            player.level().addFreshEntity(new ItemEntity(
                    player.level(),
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    cap.copy()
            ));

        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null && context.isCritical()) {

            if (random.nextFloat() <= 0.02f) {
                ItemStack cap = new ItemStack(ItemRegister.MOZHUA_CAP.get());

                player.level().addFreshEntity(new ItemEntity(
                        player.level(),
                        player.getX(),
                        player.getY() + 0.5,
                        player.getZ(),
                        cap.copy()
                ));

            }
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

}
