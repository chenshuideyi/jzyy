package com.csdy.jzyy.modifier.modifier;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.UUID;
import java.util.function.BiConsumer;

import static slimeknights.tconstruct.library.tools.stat.ToolStats.ARMOR;
import static slimeknights.tconstruct.library.tools.stat.ToolStats.ARMOR_TOUGHNESS;

public class BedRock extends NoLevelsModifier implements ToolStatsModifierHook, AttributesModifierHook {

    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        ARMOR.add(builder, 100000);
        ARMOR_TOUGHNESS.add(builder, 100000);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            UUID uuid = UUID.fromString("420103200-e5f6-7890-a1b2-5f7890");
            AttributeModifier modifier = new AttributeModifier(
                    uuid,
                    "tool_health_boost",
                    1000.0,
                    AttributeModifier.Operation.ADDITION
            );
            consumer.accept(Attributes.MAX_HEALTH, modifier);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
    }


}
