package com.csdy.jzyy.modifier.modifier.soul;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EnderManSoul extends NoLevelsModifier implements DamageBlockModifierHook, InventoryTickModifierHook {

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        if (source.getDirectEntity() instanceof Projectile){
            teleportRandomly(context.getEntity());
            return true;
        }
        return false;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (isCorrectSlot && isInWater(player,world)) {
            player.playSound(SoundEvents.ENDERMAN_TELEPORT);
            teleportRandomly(player);
            player.setHealth(player.getHealth() - 1);
        }
    }


    private boolean isInWater(LivingEntity entity,Level level) {
        if (entity.isInWaterRainOrBubble()) return true;
        return entity.isInWater();
    }

    private boolean teleportRandomly(LivingEntity entity) {
        double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
        double y = entity.getY() + (double)(entity.getRandom().nextInt(16) - 8);
        double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;

        return entity.randomTeleport(x,y,z,true);
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }



}
