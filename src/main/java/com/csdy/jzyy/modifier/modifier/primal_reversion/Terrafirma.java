package com.csdy.jzyy.modifier.modifier.primal_reversion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Random;

public class Terrafirma extends Modifier implements InventoryTickModifierHook {

    private static final Random RANDOM = new Random();

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (isCorrectSlot) {
            if (!(player.getHealth() < player.getMaxHealth())) return;
            if (RANDOM.nextFloat() < 0.05) {
                player.heal(entry.getLevel() / 3.0f);
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }


}
