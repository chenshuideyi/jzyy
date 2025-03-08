package com.csdy.modifier.diadema;

import com.csdy.DiademaModifier;
import com.csdy.diadema.DiademaRegister;
import com.csdy.effect.register.EffectRegister;
import com.csdy.frames.diadema.DiademaType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Wind extends DiademaModifier implements InventoryTickModifierHook {
    @Override
    protected DiademaType getDiademaType() {
        return DiademaRegister.WIND.get();
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack itemStack) {
        if (!(livingEntity instanceof Player player) || !isCorrectSlot) return;
        player.addEffect(new MobEffectInstance(EffectRegister.WIND.get(), 100, 0));
        player.getAbilities().setFlyingSpeed(0.2f);
        player.getAbilities().mayfly = true;
    }

    @Override
    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!(context.getEntity() instanceof Player player)) return;
        player.removeEffect(new MobEffectInstance(EffectRegister.WIND.get()).getEffect());
        player.getAbilities().setFlyingSpeed(0.05f);
        super.onUnequip(tool,entry,context);
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

}
