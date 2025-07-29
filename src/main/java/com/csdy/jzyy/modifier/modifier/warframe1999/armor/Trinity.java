package com.csdy.jzyy.modifier.modifier.warframe1999.armor;

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

import java.util.List;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.csdyAABB;

public class Trinity extends NoLevelsModifier implements InventoryTickModifierHook {
    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide()) {
            float healAmount = 4.0f;
            int cooldownTicks = 20;

            if (holder.tickCount % cooldownTicks != 0) {
                return;
            }

            List<Player> nearbyEntities = world.getEntitiesOfClass(Player.class, csdyAABB(8,holder));

            for (Player entity : nearbyEntities) {
                if (entity.isAlive()) {
                    entity.heal(healAmount);
                }
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
