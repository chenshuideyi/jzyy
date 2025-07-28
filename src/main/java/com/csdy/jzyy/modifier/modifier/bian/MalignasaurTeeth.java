package com.csdy.jzyy.modifier.modifier.bian;

import com.c2h6s.etstlib.tool.modifiers.base.EtSTBaseModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MalignasaurTeeth {

    public static class hungryhunter extends EtSTBaseModifier {


        private static final float MAX_DAMAGE_MULTIPLIER_PER_LEVEL = 0.5f;

        private static final float MAX_ATTACK_SPEED_MULTIPLIER_PER_LEVEL = 0.5f;

        private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.nameUUIDFromBytes("hungryhunter_attack_speed".getBytes(StandardCharsets.UTF_8));

        @Override
        public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
            if (holder instanceof Player player && isSelected) {
                int foodLevel = player.getFoodData().getFoodLevel();
                float hungerRatio = 1 - (float) foodLevel / 20;

                int level = modifier.getLevel();


                float additionalAttackSpeedMultiplier = MAX_ATTACK_SPEED_MULTIPLIER_PER_LEVEL * hungerRatio * level;

                AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
                if (attackSpeedAttribute != null) {
                    AttributeModifier existing = attackSpeedAttribute.getModifier(ATTACK_SPEED_MODIFIER_ID);

                    if (existing == null || existing.getAmount() != additionalAttackSpeedMultiplier) {
                        attackSpeedAttribute.removeModifier(ATTACK_SPEED_MODIFIER_ID);

                        attackSpeedAttribute.addTransientModifier(new AttributeModifier(
                                ATTACK_SPEED_MODIFIER_ID,
                                "hunger_based_attack_speed",
                                additionalAttackSpeedMultiplier,
                                AttributeModifier.Operation.MULTIPLY_BASE
                        ));
                    }
                }
            }
        }

        @Override
        public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
            if (context.getAttacker() instanceof Player player) {
                int foodLevel = player.getFoodData().getFoodLevel();
                float hungerRatio = 1 - (float) foodLevel / 20;

                int level = modifier.getLevel();


                float additionalDamageMultiplier = MAX_DAMAGE_MULTIPLIER_PER_LEVEL * hungerRatio * level;

                return damage * (1 + additionalDamageMultiplier);
            }
            return damage;
        }
    }
}