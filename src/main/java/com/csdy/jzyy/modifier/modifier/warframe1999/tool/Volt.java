package com.csdy.jzyy.modifier.modifier.warframe1999.tool;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class Volt extends NoLevelsModifier implements AttributesModifierHook {

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.HAND) {
            UUID uuid = UUID.nameUUIDFromBytes(("volt_speed" + slot.getName()).getBytes());
            AttributeModifier modifier = new AttributeModifier(
                    uuid,
                    "volt_speed",
                    0.5,
                    AttributeModifier.Operation.ADDITION
            );
            consumer.accept(Attributes.ATTACK_SPEED, modifier);
            consumer.accept(Attributes.MOVEMENT_SPEED, modifier);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
    }


}
