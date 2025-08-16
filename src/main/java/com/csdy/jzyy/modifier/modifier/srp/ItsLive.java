package com.csdy.jzyy.modifier.modifier.srp;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static slimeknights.tconstruct.library.tools.stat.ToolStats.DURABILITY;

public class ItsLive extends NoLevelsModifier implements InventoryTickModifierHook {

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level level, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!level.isClientSide()) {
            if (holder.tickCount % 600 == 0 && holder instanceof Player player) {
                if (tool.getCurrentDurability() < DURABILITY.getDefaultValue() - 200) {
                    ToolDamageUtil.repair(tool, 200);
                    holder.hurt(level.damageSources.playerAttack(player), 10);
                }
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
