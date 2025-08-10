package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Meror {

    public static class merorbroken extends NoLevelsModifier implements MeleeDamageModifierHook {


        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        }

        @Override
        public float getMeleeDamage(@NotNull IToolStackView iToolStackView, @NotNull ModifierEntry modifierEntry, @NotNull ToolAttackContext toolAttackContext, float damage, float baseDamage) {
            LivingEntity target = toolAttackContext.getLivingTarget();
            if (target != null) {
                ResourceLocation bleedingId = new ResourceLocation("jerotes", "bleeding");
                MobEffect bleedingEffect = ForgeRegistries.MOB_EFFECTS.getValue(bleedingId);
                if (bleedingEffect != null) {
                    MobEffectInstance bleeding = new MobEffectInstance(bleedingEffect, 1200, 4);
                    target.addEffect(bleeding);
                }
            }
            return damage;
        }
    }
}