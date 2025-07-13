package com.csdy.jzyy.modifier.modifier.abyss_alloy;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowHitModifierHook;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class DeepOceanBlessings extends NoLevelsModifier implements MeleeHitModifierHook, ArrowHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Player player = context.getPlayerAttacker();
        if (player != null) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (!player.getCooldowns().isOnCooldown(mainHandItem.getItem())) {
                if (player.getHealth() <= player.getMaxHealth() * 0.88F) {
                    player.heal(player.getMaxHealth() * 0.22F);
                    player.getCooldowns().addCooldown(mainHandItem.getItem(), 220);
                }
            } else {
                player.getCooldowns().isOnCooldown(mainHandItem.getItem());
            }
        }

    }

    @Override
    public void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
        if (attacker instanceof Player player) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (!player.getCooldowns().isOnCooldown(mainHandItem.getItem())) {
                if (player.getHealth() <= player.getMaxHealth() * 0.88F) {
                    player.heal(player.getMaxHealth() * 0.22F);
                    player.getCooldowns().addCooldown(mainHandItem.getItem(), 220);
                }
            } else {
                player.getCooldowns().isOnCooldown(mainHandItem.getItem());
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_HIT);
    }
}
