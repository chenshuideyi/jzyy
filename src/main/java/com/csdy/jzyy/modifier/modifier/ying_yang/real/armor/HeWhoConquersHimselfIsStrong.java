package com.csdy.jzyy.modifier.modifier.ying_yang.real.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.UUID;
import java.util.function.BiConsumer;

public class HeWhoConquersHimselfIsStrong extends NoLevelsModifier implements AttributesModifierHook, VolatileDataModifierHook {
    ///自胜者强

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            // 最大生命值翻倍（乘法）
            UUID healthUuid = UUID.nameUUIDFromBytes((this.getClass().getSimpleName() + "_health_" + slot.getName()).getBytes());
            AttributeModifier healthModifier = new AttributeModifier(
                    healthUuid,
                    "yin_yang_health_multiply",
                    1.0, // 增加100%，即翻倍
                    AttributeModifier.Operation.MULTIPLY_BASE
            );
            consumer.accept(Attributes.MAX_HEALTH, healthModifier);

            // 免疫击退（100%击退抗性）
            UUID knockbackUuid = UUID.nameUUIDFromBytes(("tool_knockback_resistance_" + slot.getName()).getBytes());
            AttributeModifier knockbackModifier = new AttributeModifier(
                    knockbackUuid,
                    "yin_yang_knockback_resistance",
                    1.0, // 100%击退抗性
                    AttributeModifier.Operation.ADDITION
            );
            consumer.accept(Attributes.KNOCKBACK_RESISTANCE, knockbackModifier);
        }

    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry entry, ToolDataNBT volatileData) {
        volatileData.addSlots(SlotType.ABILITY, 2);
        volatileData.addSlots(SlotType.DEFENSE, 2);
        volatileData.addSlots(SlotType.UPGRADE, 2);
        volatileData.addSlots(SlotType.SOUL, 2);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }


}
