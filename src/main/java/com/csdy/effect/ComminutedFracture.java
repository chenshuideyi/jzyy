package com.csdy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ComminutedFracture extends MobEffect {
    private static final String SPEED_MODIFIER_UUID = "7107DE5E-7CE8-4030-940E-514C1F160890"; // 唯一标识符
    private static final double SPEED_REDUCTION = -1;

    public ComminutedFracture() {
        super(MobEffectCategory.HARMFUL, 0);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID, SPEED_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);

    }

//    @Override
//    public void applyEffectTick(LivingEntity entity, int amplifier) {
//
//    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap map,int amplifier) {
        if (entity instanceof Player player) {
            // 获取玩家当前最大生命值
            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                // 计算减少后的生命值
                double originalMaxHealth = maxHealthAttr.getBaseValue();
                double reducedMaxHealth = originalMaxHealth * 0.5; // 减少50%

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
                double restoredMaxHealth = originalMaxHealth * 2.0; // 恢复为原始值
                maxHealthAttr.setBaseValue(restoredMaxHealth);
            }
        }
    }

//    @Override
//    public boolean isDurationEffectTick(int duration, int amplifier) {
//        return false; // 每 tick 都执行
//    }
}
