package com.csdy.jzyy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OverchargeArmor extends MobEffect {

    private static final String ARMOR_MODIFIER_UUID = "7107DE5E-7CE8-4030-940E-514C1F160893"; // 唯一标识符
    private static final double ARMOR_REDUCTION = 0.8;

    private static final String MOVEMENT_SPEED_MODIFIER_UUID = "7107DE5E-7CE8-4030-940E-514C1F160893";
    private static final double MOVEMENT_SPEED_REDUCTION = 0.8;

    public OverchargeArmor() {
        super(MobEffectCategory.BENEFICIAL, 0);
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER_UUID, ARMOR_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_UUID, MOVEMENT_SPEED_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

}
