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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DotArmor extends NoLevelsModifier implements OnAttackedModifierHook, ModifyDamageModifierHook,EquipmentChangeModifierHook {
    private static final int POWER_DURATION = 200;
    private Object eventListener;
    private static final float MIN_THORNS_MULTIPLIER = 15.0f;
    private static final float MAX_THORNS_MULTIPLIER = 25.0f;



    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onAttacked(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentContext context, @NotNull EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (context == null || context.getEntity() == null || !(context.getEntity() instanceof Player player)) {
            return;
        }

        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof LivingEntity living) || living.isRemoved()) {
            return;
        }


        List<MobEffect> negativeEffects = new ArrayList<>();
        for (var key : ForgeRegistries.MOB_EFFECTS.getKeys()) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
            if (effect != null && effect.getCategory() == MobEffectCategory.HARMFUL) {
                negativeEffects.add(effect);
            }
        }

        if (!negativeEffects.isEmpty()) {
            Random random = new Random();

            int effectsToApply = 2 + random.nextInt(3);
            List<MobEffect> selectedEffects = new ArrayList<>();


            for (int i = 0; i < effectsToApply && !negativeEffects.isEmpty(); i++) {
                int index = random.nextInt(negativeEffects.size());
                selectedEffects.add(negativeEffects.remove(index));
            }


            for (MobEffect effect : selectedEffects) {
                living.addEffect(new MobEffectInstance(effect, 200 * 10 * 5, 5));
            }
        }


        Random random = new Random();
        float thornsMultiplier = MIN_THORNS_MULTIPLIER + random.nextFloat() * (MAX_THORNS_MULTIPLIER - MIN_THORNS_MULTIPLIER);
        living.hurt(player.damageSources().thorns(player), amount * thornsMultiplier);
    }


    @Override
    public void onEquip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier,@Nonnull EquipmentChangeContext context) {
        if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {

            player.getPersistentData().putBoolean("dot2_equipped", true);

            eventListener = new Object() {
                @SubscribeEvent
                public void onPlayerTick(TickEvent.PlayerTickEvent event) {
                    if (event.phase == TickEvent.Phase.START && event.player != null && !event.player.isRemoved()
                            && event.player == player && player.getPersistentData().getBoolean("dot2_equipped")) {

                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, POWER_DURATION, 3));

                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, POWER_DURATION, 9));

                        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, POWER_DURATION, 9));

                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, POWER_DURATION, 9));

                        LivingEntityUtil.forceRemoveAllNegativeEffects(player);
                    }
                }
            };
            MinecraftForge.EVENT_BUS.register(eventListener);
        }
    }

    @Override
    public void onUnequip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
        if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
            player.getPersistentData().putBoolean("dot2_equipped", false);
            MinecraftForge.EVENT_BUS.unregister(this);
            MinecraftForge.EVENT_BUS.unregister(eventListener);
        }
    }

    private boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        }


    public float modifyDamageTaken(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentContext context, @Nonnull EquipmentSlot slot, @Nonnull DamageSource damageSource, float amount, boolean isDirectDamage) {


        if (context == null || context.getEntity() == null) {
            return amount;
        }


        if (!(context.getEntity() instanceof Player player)) {
            return amount;
        }

        return amount * 0.0f;
    }
}
