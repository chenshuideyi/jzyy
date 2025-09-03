package com.csdy.jzyy.modifier.modifier.boring;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class Range extends Modifier implements AttributesModifierHook {

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.HAND) {

            // 提升攻击距离（使用ENTITY_REACH）
            UUID reachUuid = UUID.nameUUIDFromBytes(("tool_attack_reach_" + slot.getName()).getBytes());
            AttributeModifier reachModifier = new AttributeModifier(
                    reachUuid,
                    "range_attack_reach",
                    3.0 * entry.getLevel(), // 增加3格攻击距离
                    AttributeModifier.Operation.ADDITION
            );
            consumer.accept(ForgeMod.ENTITY_REACH.get(), reachModifier);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
    }
}
