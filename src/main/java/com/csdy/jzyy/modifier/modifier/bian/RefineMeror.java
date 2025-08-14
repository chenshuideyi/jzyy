package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RefineMeror extends NoLevelsModifier implements EquipmentChangeModifierHook, InventoryTickModifierHook {
    private static final double DEFAULT_RADIUS = 6.0;
    private double currentRadius = DEFAULT_RADIUS;

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }


    private void performAutoAttack(Player player) {
        for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(currentRadius))) {
            if (isValidTarget(player, entity)) {
                player.attack(entity);
                entity.invulnerableTime = 0;
            }
        }
    }

    private boolean isValidTarget(Player player, Entity entity) {
        return entity instanceof LivingEntity && 
               !entity.isAlliedTo(player) && 
               entity.isAlive() && 
               player.distanceTo(entity) <= currentRadius;
    }

    public void setAttackRadius(double radius) {this.currentRadius = Math.max(0, radius);}

    @Override
    public void onInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {

        if (holder instanceof Player player && ItemStack.matches(stack, player.getMainHandItem())) {
            performAutoAttack(player);
        }
    }
}