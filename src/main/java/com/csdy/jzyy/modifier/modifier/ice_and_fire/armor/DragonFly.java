package com.csdy.jzyy.modifier.modifier.ice_and_fire.armor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class DragonFly extends NoLevelsModifier implements InventoryTickModifierHook, EquipmentChangeModifierHook {

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        // 飞行能力逻辑
        if (!isCorrectSlot) return;
        if (holder instanceof Player player) {
            enableFlightForPlayer(player);
            player.addEffect(new MobEffectInstance(
                    MobEffects.HUNGER,
                    40,  // 2秒持续时间（40 ticks）
                    2,   // 等级III（0=I, 1=II, 2=III）
                    false,  // 无粒子效果
                    false,  // 不显示图标
                    true    // 来自物品的效果
            ));
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        var entity = context.getEntity();
        if (entity instanceof Player player){
            player.getAbilities().mayfly = false;
        }
    }

    private void enableFlightForPlayer(Player player) {
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
