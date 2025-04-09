package com.csdy.jzyy.modifier.modifier.abyss_alloy.armor;

import com.csdy.jzyy.font.RainbowText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class DeepOceanProtect extends NoLevelsModifier implements ModifyDamageModifierHook, OnAttackedModifierHook, TooltipModifierHook {

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;
        float truth = (float) Math.round((player.getMaxHealth() * 0.3F + 1.0F) * ((float) player.totalExperience * 1.0E-4F + 1.0F) * ((float) player.getArmorValue() * 0.6F + 1.0F));
        if (!player.getCooldowns().isOnCooldown(tool.getItem())) {
            player.heal(player.getMaxHealth() / 3);
            player.setAbsorptionAmount(Math.min(player.getMaxHealth() * 0.5F + player.getAbsorptionAmount(), truth / 100.0F));
            player.getCooldowns().addCooldown(tool.getItem(), 100);
        }
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return amount;
        float truth = (float) Math.round((player.getMaxHealth() * 0.3F + 1.0F) * ((float) player.totalExperience * 1.0E-4F + 1.0F) * ((float) player.getArmorValue() * 0.6F + 1.0F));
        return amount * Math.max(1.0F - truth * 0.01F, 0.05F);
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, slimeknights.mantle.client.TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (player != null) {
            float truth = (float)Math.round((player.getMaxHealth() * 0.3F + 1.0F) * ((float)player.totalExperience * 1.0E-4F + 1.0F) * ((float)player.getArmorValue() * 0.6F + 1.0F));
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("当前回声点数")).append(RainbowText.DeepOceanEcho(""+truth)));
            float var10003 = Math.min(truth * 0.001F, 0.95F);
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("当前伤害减免")).append(RainbowText.DeepOceanEcho(var10003*100F+"%")));

            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("当前受到伤害给予的伤害吸收")).append(RainbowText.DeepOceanEcho(String.valueOf(player.getMaxHealth() * 0.5F))));
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("伤害吸收上限")).append(RainbowText.DeepOceanEcho(String.valueOf(truth / 100.0F))));
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
    }
}
