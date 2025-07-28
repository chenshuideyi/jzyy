package com.csdy.jzyy.modifier.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Infinitum extends NoLevelsModifier implements ToolDamageModifierHook, InventoryTickModifierHook {

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry entry, int amount, @Nullable LivingEntity holder) {
        return 0;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        tool.setDamage(0);
        tool.isUnbreakable();
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE,ModifierHooks.INVENTORY_TICK);
    }
}
