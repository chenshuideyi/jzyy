package com.csdy.jzyy.modifier.modifier.abyss_alloy;

import com.c2h6s.etstlib.entity.specialDamageSources.PercentageBypassArmorSource;
import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowHitModifierHook;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class TheProphecyOfTheSunk extends NoLevelsModifier implements MeleeHitModifierHook, ArrowHitModifierHook {



    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            if (target.getHealth() >= target.getMaxHealth() * 0.8F) {
                target.setHealth(target.getHealth() - target.getMaxHealth() * 0.7F);
            } else {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1000, 2, false, false));
            }
        }

    }


    @Override
    public void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
        if (!(attacker instanceof Player player)) return;
        if (target.getHealth() >= target.getMaxHealth() * 0.8F) {
            target.setHealth(target.getHealth() - target.getMaxHealth() * 0.7F);
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1000, 2, false, false));
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_HIT);
    }
}
