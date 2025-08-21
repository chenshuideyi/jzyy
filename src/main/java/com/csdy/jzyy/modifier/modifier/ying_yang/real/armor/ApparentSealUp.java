package com.csdy.jzyy.modifier.modifier.ying_yang.real.armor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceRemoveAllNegativeEffects;

public class ApparentSealUp extends NoLevelsModifier implements InventoryTickModifierHook {
    ///如封似闭

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;

        if (isCorrectSlot) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,  // 生命恢复
                    20, 254,
                    false, false, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,  // 抗性提升
                    20, 4,
                    false, false, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,  // 夜视
                    600, 0,
                    false, false, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.SATURATION,    // 饱和
                    20, 0,
                    false, false, true
            ));
            player.abilities.flying = true;
        }
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }


}
