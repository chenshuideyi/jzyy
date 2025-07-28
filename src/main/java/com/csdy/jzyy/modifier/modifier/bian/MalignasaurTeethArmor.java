package com.csdy.jzyy.modifier.modifier.bian;

import com.c2h6s.etstlib.util.EquipmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;


public class MalignasaurTeethArmor {

    public static class ancientrespiration extends Modifier implements EquipmentChangeModifierHook, OnAttackedModifierHook, ModifyDamageModifierHook, AttributesModifierHook {

        private static final float DAMAGE_REDUCTION_PER_LEVEL = 0.16f;
        private static final String IMMUNITY_KEY = "ancientrespiration_equipped";
        private static final float MELEE_DAMAGE_BOOST_PER_LEVEL = 2.5f;
        private static final UUID damageUuid = UUID.nameUUIDFromBytes("ancientrespiration_attack".getBytes(StandardCharsets.UTF_8));

        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
            hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
            super.registerHooks(hookBuilder);
        }

        @Override
        public float modifyDamageTaken(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentContext context, @NotNull EquipmentSlot slot, @NotNull DamageSource source, float damage, boolean isDirectDamage) {
            if (isDrowningDamage(source)) {
                int level = modifier.getLevel();
                float reduction = level * DAMAGE_REDUCTION_PER_LEVEL;
                return damage * (1 - reduction);
            }
            return damage;
        }

        @Override
        public void onAttacked(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentContext context, @NotNull EquipmentSlot slot, @NotNull DamageSource source, float originalDamage, boolean isDirectDamage) {
            if (isDrowningDamage(source)) {
                int level = modifier.getLevel();
                float reduction = level * DAMAGE_REDUCTION_PER_LEVEL;
                float absorbedDamage = originalDamage * reduction;
                LivingEntity entity = context.getEntity();
                entity.heal(absorbedDamage);
            }
        }

        @Override
        public void addAttributes(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentSlot slot, @NotNull BiConsumer<Attribute, AttributeModifier> consumer) {

            if (!EquipmentUtil.ARMOR.contains(slot)) return;

            int level = modifier.getLevel();

            consumer.accept(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(damageUuid, "Ancient respiration damage boost", level * MELEE_DAMAGE_BOOST_PER_LEVEL, AttributeModifier.Operation.ADDITION)
            );
        }

        @Override
        public void onEquip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot())) {
                LivingEntity entity = context.getEntity();
                if (entity instanceof Player player) {
                    player.getPersistentData().putBoolean(IMMUNITY_KEY, true);
                }
            }
        }

        @Override
        public void onUnequip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot())) {
                LivingEntity entity = context.getEntity();
                if (entity instanceof Player player) {
                    player.getPersistentData().putBoolean(IMMUNITY_KEY, false);
                }
            }
        }

        private boolean isDrowningDamage(DamageSource source) {
            Holder<DamageType> damageType = source.typeHolder();
            return damageType.is(DamageTypeTags.IS_DROWNING);
        }

        private static boolean isArmorSlot(EquipmentSlot slot) {
            return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        }
    }
}
