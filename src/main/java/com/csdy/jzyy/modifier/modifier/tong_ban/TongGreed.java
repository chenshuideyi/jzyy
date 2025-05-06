package com.csdy.jzyy.modifier.modifier.tong_ban;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.Map;

public class TongGreed extends Modifier implements EnchantmentModifierHook, ProjectileHitModifierHook, MeleeHitModifierHook {

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry entry, Enchantment enchantment, int level) {
        return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry entry, Map<Enchantment, Integer> map) {
        map.put(Enchantments.MOB_LOOTING,entry.getLevel());
        map.put(Enchantments.BLOCK_FORTUNE,entry.getLevel());
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.isFullyCharged() && context.isCritical() && damageDealt > 0) {
            // heals a percentage of damage dealt, using same rate as reinforced
            int level = modifier.getLevel();
            float percent = 0.05f * level;
            if (percent > 0) {
                LivingEntity attacker = context.getAttacker();
                attacker.heal(percent * damageDealt);
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
                // take a bit of extra damage to heal
                ToolDamageUtil.damageAnimated(tool, level, attacker, context.getSlotType());
                spawnExperienceOrb(attacker,level);
            }
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (target != null && attacker != null) {
            float percent = 0.05f * entry.getLevel();
            if (percent > 0) {
                if (projectile instanceof AbstractArrow arrow && arrow.isCritArrow()) {
                    // we don't actually know how much damage will be dealt, so just guess by using the standard formula
                    // to prevent healing too much, limit by the target's health. Will let you life steal ignoring armor, but eh, only so much we can do efficiently
                    attacker.heal((float)(percent * Math.min(target.getHealth(), arrow.getBaseDamage() * arrow.getDeltaMovement().length())));
                    attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
                    spawnExperienceOrb(attacker,entry.getLevel());
                }
            }
        }
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ENCHANTMENTS);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT);
    }


    private void spawnExperienceOrb(LivingEntity entity,int modifierLevel) {
        Level level = entity.level();
        ExperienceOrb experienceOrb = new ExperienceOrb(level, entity.getX(), entity.getY(), entity.getZ(), 20*modifierLevel);
        level.addFreshEntity(experienceOrb);

    }
}
