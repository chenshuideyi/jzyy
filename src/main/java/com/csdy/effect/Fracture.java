package com.csdy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class Fracture extends MobEffect {
    private static final String SPEED_MODIFIER_UUID = "7107DE5E-7CE8-4030-940E-514C1F160890"; // 唯一标识符
    private static final double SPEED_REDUCTION = -0.4;
    private static final String RANGE_UUID = "7107DE5E-7CE8-4030-940E-514C1F160890"; // 唯一标识符
    private static final double RANGE_REDUCTION = 5;
    public Fracture() {
        super(MobEffectCategory.HARMFUL, 0);
        this.addAttributeModifier(Attributes.FOLLOW_RANGE, RANGE_UUID, RANGE_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID, SPEED_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        if (entity instanceof Player player) {
            // 获取玩家当前最大生命值
            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                // 计算减少后的生命值
                double originalMaxHealth = maxHealthAttr.getBaseValue();
                double reducedMaxHealth = originalMaxHealth * 0.8; // 减少20%

                // 设置新的最大生命值
                maxHealthAttr.setBaseValue(reducedMaxHealth);

                // 如果当前生命值超过新的最大生命值，则调整当前生命值
                if (player.getHealth() > reducedMaxHealth) {
                    player.setHealth((float) reducedMaxHealth);
                }
            }
        }
    }
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map,int amplifier) {
        if (entity instanceof Player player) {
            // 恢复玩家最大生命值
            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double originalMaxHealth = maxHealthAttr.getBaseValue();
                double restoredMaxHealth = originalMaxHealth * 1.25; // 恢复为原始值
                maxHealthAttr.setBaseValue(restoredMaxHealth);
            }
        }
    }
}
