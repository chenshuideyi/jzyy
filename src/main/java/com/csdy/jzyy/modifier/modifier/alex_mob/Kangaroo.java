package com.csdy.jzyy.modifier.modifier.alex_mob;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Random;

public class Kangaroo extends NoLevelsModifier implements DamageBlockModifierHook {

    private static final Random random = new Random();
    private static final double CHANCE = 0.4;

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        if (!source.is(DamageTypes.ARROW)) return false;
        if (!(context.getEntity() instanceof Player player)) return false;
        if (random.nextDouble() < CHANCE) {
            jump(player);
            return true;
        }
        return false;
    }


    private void jump(Player player) {
        if (player.level().isClientSide) return;
        double dashSpeed = 0.6;
        player.setDeltaMovement(
                player.getDeltaMovement().x,
                player.getDeltaMovement().y + dashSpeed,
                player.getDeltaMovement().z
        );
        player.hurtMarked = true; // 强制同步运动数据,很重要
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
    }

}
