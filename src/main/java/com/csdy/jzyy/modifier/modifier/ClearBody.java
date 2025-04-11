package com.csdy.jzyy.modifier.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.util.LivingEntityUtil.forceRemoveAllNegativeEffects;

public class ClearBody extends NoLevelsModifier implements InventoryTickModifierHook {

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (isCorrectSlot) {
            forceRemoveAllNegativeEffects(player);
        }
    }



    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
