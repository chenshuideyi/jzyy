package com.csdy.jzyy.modifier.modifier.bian;

import com.csdy.jzyy.ms.util.LivingEntityUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DotArmor extends NoLevelsModifier implements OnAttackedModifierHook, ModifyDamageModifierHook, EquipmentChangeModifierHook, InventoryTickModifierHook {
    private static final int POWER_DURATION = 200;
    private static final int ATTACKED_EFFECT_DURATION = 10000;
    private static final float MIN_THORNS_MULTIPLIER = 15.0F;
    private static final float MAX_THORNS_MULTIPLIER = 25.0F;
    private static final List<MobEffect> NEGATIVE_EFFECTS_CACHE;
    static {
        List<MobEffect> effects = new ArrayList<>();
        for (var key : ForgeRegistries.MOB_EFFECTS.getKeys()) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
            if (effect != null && effect.getCategory() == MobEffectCategory.HARMFUL) {
                effects.add(effect);
            }
        }
        NEGATIVE_EFFECTS_CACHE = Collections.unmodifiableList(effects);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onAttacked(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentContext context, @NotNull EquipmentSlot slot, @NotNull DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;

        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof LivingEntity living) || living.isRemoved()) return;

        if (!NEGATIVE_EFFECTS_CACHE.isEmpty()) {
            int count = 2 + ThreadLocalRandom.current().nextInt(3);
            List<MobEffect> shuffled = new ArrayList<>(NEGATIVE_EFFECTS_CACHE);
            Collections.shuffle(shuffled, ThreadLocalRandom.current());
            for (int i = 0; i < count && i < shuffled.size(); i++) {
                living.addEffect(new MobEffectInstance(shuffled.get(i), ATTACKED_EFFECT_DURATION, 5));
            }
        }

        float thornsMultiplier = MIN_THORNS_MULTIPLIER + ThreadLocalRandom.current().nextFloat() * (MAX_THORNS_MULTIPLIER - MIN_THORNS_MULTIPLIER);
        living.hurt(player.damageSources().thorns(player), amount * thornsMultiplier);
    }

    @Override
    public void onEquip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
        if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();

        }
    }

    @Override
    public void onUnequip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
        if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    @Override
    public float modifyDamageTaken(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentContext context, @Nonnull EquipmentSlot slot, @Nonnull DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (context.getEntity() instanceof Player && isArmorSlot(slot)) {
            return 0;
        }
        return amount;
    }

    @Override
    public void onInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        if (!(holder instanceof Player player) || !isCorrectSlot) return;
        LivingEntityUtil.forceRemoveAllNegativeEffects(player);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, POWER_DURATION, 4));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, POWER_DURATION, 9));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, POWER_DURATION, 9));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, POWER_DURATION, 9));
    }



    private boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }
}