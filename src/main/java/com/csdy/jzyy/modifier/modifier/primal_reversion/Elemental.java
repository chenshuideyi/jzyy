package com.csdy.jzyy.modifier.modifier.primal_reversion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
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

import java.util.*;

public class Elemental extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook {
    private static final List<MobEffect> BENEFICIAL_EFFECTS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if (effect.getCategory() == MobEffectCategory.BENEFICIAL) {
                BENEFICIAL_EFFECTS.add(effect);
//                System.out.println("添加一个"+effect.getClass().getName());
            }
        }
    }

    public static MobEffect getRandomBeneficialEffect() {
        return BENEFICIAL_EFFECTS.get(RANDOM.nextInt(BENEFICIAL_EFFECTS.size()));
    }

    public static void applyRandomEffects(Player player, int count, int duration, int amplifier) {
        Set<MobEffect> chosenEffects = new HashSet<>();
        while (chosenEffects.size() < count) {
            chosenEffects.add(getRandomBeneficialEffect());
        }
        chosenEffects.forEach(effect ->
                player.addEffect(new MobEffectInstance(effect, duration, amplifier))
        );
    }


    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            applyRandomEffects(player, 2, 60, 0);
        }

    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT data, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity shooter, @javax.annotation.Nullable LivingEntity target) {
        if (projectile instanceof AbstractArrow arrow && target != null) {
            if (!(shooter instanceof Player player)) return false;
            applyRandomEffects(player, 2, 60, 0);
            arrow.remove(Entity.RemovalReason.KILLED);
        }
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }



}
