package com.csdy.jzyy.modifier.modifier.fired_chicken;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Kfc extends NoLevelsModifier implements GeneralInteractionModifierHook {

    @Override
    public @NotNull UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry entry, Player player, InteractionHand hand, InteractionSource interactionSource) {
        if (!tool.isBroken()) {
            GeneralInteractionModifierHook.startUsing(tool, entry.getId(), player, hand);
            return InteractionResult.CONSUME;
        }else return InteractionResult.PASS;

    }

    @Override
    public void onFinishUsing (IToolStackView tool, ModifierEntry entry, LivingEntity entity){
        if (!(entity instanceof Player player)) return;
        player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() + 6);

        player.addEffect(new MobEffectInstance(
                MobEffects.DIG_SPEED,
                600,
                1,
                false,
                false,
                true
        ));
    }

    @Override
    protected void registerHooks (ModuleHookMap.Builder hookBuilder){
        hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
    }

}
