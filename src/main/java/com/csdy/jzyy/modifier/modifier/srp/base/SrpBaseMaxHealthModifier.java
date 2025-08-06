package com.csdy.jzyy.modifier.modifier.srp.base;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class SrpBaseMaxHealthModifier extends Modifier implements AttributesModifierHook {

    private final String name;
    private final float healthAmount;

    public SrpBaseMaxHealthModifier(String name, float healthAmount) {
        this.name = name;
        this.healthAmount = healthAmount;
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            UUID uuid = UUID.nameUUIDFromBytes((name + slot.getName()).getBytes());
            AttributeModifier modifier = new AttributeModifier(
                    uuid,
                    name + "health_boost",
                    healthAmount * entry.getLevel(),
                    AttributeModifier.Operation.ADDITION
            );
            consumer.accept(Attributes.MAX_HEALTH, modifier);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
    }
}
