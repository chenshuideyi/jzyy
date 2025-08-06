package com.csdy.jzyy.modifier.modifier.eva;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.*;

public class Longinus extends Modifier implements MeleeHitModifierHook, ProjectileHitModifierHook {


    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        var level = entry.getLevel();
        if (target != null && player != null) {
            if (isFromDummmmmmyMod(target)) return;
            modifierSeverance(target,player,1000 * ToolStats.ATTACK_DAMAGE.getDefaultValue() * (level * 2 - 1) ,1);
        }

    }


    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT data, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity shooter, @javax.annotation.Nullable LivingEntity target) {
        if (projectile instanceof AbstractArrow arrow && target != null) {
            if (!(shooter instanceof Player player)) return false;
            if (isFromDummmmmmyMod(target)) return false;
            modifierSeverance(target,player,1000 * ToolStats.ATTACK_DAMAGE.getDefaultValue() * (entry.getLevel() * 2 - 1) ,1);
        }
        return false;
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }



}
