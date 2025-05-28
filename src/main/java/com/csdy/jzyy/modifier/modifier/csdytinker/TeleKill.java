package com.csdy.jzyy.modifier.modifier.csdytinker;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isEntityInSunlight;

public class TeleKill extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (target instanceof Mob mob) {
            mob.setNoAi(true);
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT data, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity shooter, @javax.annotation.Nullable LivingEntity target) {
        if (target != null) {
            if (target instanceof Mob mob) {
                mob.setNoAi(true);
            }
        }
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }
}
