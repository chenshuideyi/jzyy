package com.csdy.jzyy.modifier.modifier.ice_and_fire.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class IceDragonArmor extends NoLevelsModifier implements ModifyDamageModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
    }

    // 最低伤害比例 (当速度为0时)
    private static final float MIN_DAMAGE_RATIO = 0.3f;
    // 完全生效的移动速度阈值 (高于此值不减免伤害)
    private static final float FULL_DAMAGE_SPEED = 0.15f;

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        var holder = context.getEntity();
        float movementAttribute =  (float) holder.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float damageRatio = calculateDamageRatio(movementAttribute);
        return amount * damageRatio; // 完全免疫雷电伤害

    }

    private static float calculateDamageRatio(float speed) {
        // 如果速度高于阈值，不减免伤害
        if (speed >= FULL_DAMAGE_SPEED) {
            return 1.0f;
        }

        // 线性计算伤害比例
        float ratio = MIN_DAMAGE_RATIO + (1 - MIN_DAMAGE_RATIO) * (speed / FULL_DAMAGE_SPEED);
        return Math.max(ratio, MIN_DAMAGE_RATIO);
    }

}

