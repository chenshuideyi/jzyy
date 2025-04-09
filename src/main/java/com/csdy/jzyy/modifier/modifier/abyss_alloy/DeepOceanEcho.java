package com.csdy.jzyy.modifier.modifier.abyss_alloy;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import com.csdy.jzyy.font.RainbowText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.List;

public class DeepOceanEcho extends NoLevelsModifier implements MeleeDamageModifierHook, ArrowDamageModifierHook,TooltipModifierHook {

//    private static final ResourceLocation DEEP_OCEAN_ECHO = new ResourceLocation(ModMain.MODID, "deep_ocean_echo");

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        int level = entry.getLevel();
        if (target == null) return baseDamage;
        if (attacker instanceof Player player) {
            float a = (player.getMaxHealth() * 0.3F + 1.0F) * ((float)player.totalExperience * 1.0E-4F + 1.0F) * ((float)player.getArmorValue() * 0.6F + 1.0F);
            target.invulnerableTime = 0;
            float value = attacker.getHealth() / attacker.getMaxHealth();
            return target instanceof Player ? damage * 0.0F : damage + a * 0.5F * (float)level * (value + 1.0F);
        }
        return damage;
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        int level = entry.getLevel();
        if (attacker instanceof Player player && target instanceof LivingEntity living) {
            float a = (player.getMaxHealth() * 0.3F + 1.0F) * ((float)player.totalExperience * 1.0E-4F + 1.0F) * ((float)player.getArmorValue() * 0.6F + 1.0F);
            living.invulnerableTime = 0;
            float value = attacker.getHealth() / attacker.getMaxHealth();
            return living instanceof Player ? damage * 0.0F : damage + a * 0.5F * (float)level * (value + 1.0F);
        }
        return damage;
    }


    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, slimeknights.mantle.client.TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (player != null) {
            int level = entry.getLevel();
            float echo = (float)Math.round((player.getMaxHealth() * 0.3F + 1.0F) * ((float)player.totalExperience * 1.0E-4F + 1.0F) * ((float)player.getArmorValue() * 0.6F + 1.0F));
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("当前回声点数")).append(RainbowText.DeepOceanEcho(""+echo)));
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("每点回声所增幅的伤害")).append(RainbowText.DeepOceanEcho((float)level * 0.5F + "攻击力")));
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("实际提升的总伤害")).append(RainbowText.DeepOceanEcho((float)level * 0.5F * echo + "攻击力")));
            float f = player.getHealth() / player.getMaxHealth();
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("当前专注效果增伤的百分比")).append(RainbowText.DeepOceanEcho(f * 100.0F + "%")));
            tooltip.add(Component.literal(RainbowText.DeepOceanEcho("你已是完全之龙，足以审判众神")).append(RainbowText.DeepOceanEcho("你已经掌握" + level + "层权能")));
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }


}
