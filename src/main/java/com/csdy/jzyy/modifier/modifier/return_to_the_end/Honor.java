package com.csdy.jzyy.modifier.modifier.return_to_the_end;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

public class Honor extends NoLevelsModifier implements MeleeDamageModifierHook, InventoryTickModifierHook, VolatileDataModifierHook {

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry entry, ToolDataNBT volatileData) {
        volatileData.addSlots(SlotType.ABILITY, 1);
        volatileData.addSlots(SlotType.DEFENSE, 1);
        volatileData.addSlots(SlotType.UPGRADE, 1);
        volatileData.addSlots(SlotType.SOUL, 1);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        if (!(context.getAttacker() instanceof Player player)) return damage;
        var living = context.getLivingTarget();
        if (living == null) return damage;
        float health = player.getHealth();
        float targetHealth = context.getLivingTarget().getMaxHealth();
        float value = health / targetHealth * 0.3721f;
        return damage + value;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        player.abilities.mayfly = true;
        if (isCorrectSlot && player.getHealth() > player.getMaxHealth() * 0.3721f) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4));
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }

}
