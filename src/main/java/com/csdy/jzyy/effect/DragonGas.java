package com.csdy.jzyy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class DragonGas extends MobEffect {

    private static final UUID ARMOR_MODIFIER_UUID = UUID.randomUUID();
    private static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.randomUUID();

    private static final double ARMOR_REDUCTION = -1.0; // 护甲变为 0
    private static final double ATTACK_DAMAGE_BOOST = 1.0; // 攻击力翻倍

    public DragonGas() {
        super(MobEffectCategory.NEUTRAL, 0);
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER_UUID.toString(), ARMOR_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_UUID.toString(), ATTACK_DAMAGE_BOOST, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}

