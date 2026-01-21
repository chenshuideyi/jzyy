package com.csdy.jzyy.modifier.modifier.valstrax.armor;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.modifier.register.JzyyModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceRemoveAllNegativeEffects;

public class DragonGasModifier extends JzyyModifier {

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (isCorrectSlot && player.getHealth() < player.getMaxHealth() * 0.8F) {
            player.addEffect(new MobEffectInstance(JzyyEffectRegister.DRAGON_GAS.get(), 600, 0));
        }
    }


}
