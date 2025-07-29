package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MarineReptilesFineScale {

    public static class eternalhunger extends Modifier implements EquipmentChangeModifierHook, ModifyDamageModifierHook {


        private static final int HUNGER_DURATION = 20 * 60 * 5;

        private static final int MAX_HUNGER_LEVEL = 5;

        private static final float DAMAGE_REDUCTION_PER_LEVEL = 0.16f;

        private static final int CHECK_INTERVAL_BASE = 20;

        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            super.registerHooks(hookBuilder);
        }

        @Override
        public float modifyDamageTaken(@NotNull IToolStackView iToolStackView, @NotNull ModifierEntry modifierEntry, @NotNull EquipmentContext equipmentContext, @NotNull EquipmentSlot equipmentSlot, @NotNull DamageSource damageSource, float v, boolean b) {
            if (isStarvationDamage(damageSource)) {

                int level = modifierEntry.getLevel();
                float reduction = level * DAMAGE_REDUCTION_PER_LEVEL;

                return v * (1 - reduction);
            }

            return v;
        }


        @Override
        public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentChangeContext context) {

            handleHungerEffect(context.getEntity(), modifier.getLevel());
        }

        @Override
        public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentChangeContext context) {

            LivingEntity entity = context.getEntity();
            entity.removeEffect(MobEffects.HUNGER);
        }



        private void handleHungerEffect(LivingEntity entity, int level) {

            if (entity.tickCount % (CHECK_INTERVAL_BASE / level) == 0) {
                MobEffectInstance currentEffect = entity.getEffect(MobEffects.HUNGER);
                if (currentEffect == null) {
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, 0));
                } else if (currentEffect.getAmplifier() < MAX_HUNGER_LEVEL) {
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, HUNGER_DURATION, currentEffect.getAmplifier() + 1));
                }
            }
        }

        public boolean isStarvationDamage(DamageSource source) {

            if (source.getEntity() == null) return false;

            Registry<DamageType> damageTypes = source.getEntity().level().registryAccess().registry(Registries.DAMAGE_TYPE).orElse(null);
            if (damageTypes == null) return false;


            ResourceLocation starveLoc = new ResourceLocation("minecraft:starve");
            return damageTypes.getResourceKey(source.type()).orElse(null) == ResourceKey.create(Registries.DAMAGE_TYPE, starveLoc);
        }
    }
}
