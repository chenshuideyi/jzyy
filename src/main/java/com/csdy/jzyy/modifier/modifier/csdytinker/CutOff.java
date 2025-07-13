package com.csdy.jzyy.modifier.modifier.csdytinker;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CutOff extends NoLevelsModifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (target == null) return;
        var level = target.level;
        if (!level.isClientSide) {
            ItemStack mainHandStack = target.getMainHandItem();
            if (!mainHandStack.isEmpty()) {
                ItemStack itemToDrop = mainHandStack.copy();

                target.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

                double x = target.getX();
                double y = target.getY() + target.getEyeHeight() / 2.0;
                double z = target.getZ();

                ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemToDrop);
                itemEntity.setPickUpDelay(40);

                float f = target.random.nextFloat() * 0.5F;
                float f1 = target.random.nextFloat() * ((float)Math.PI * 2F);
                itemEntity.setDeltaMovement(-Math.sin(f1) * f, 0.2F, Math.cos(f1) * f);

                level.addFreshEntity(itemEntity);

                level.playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
