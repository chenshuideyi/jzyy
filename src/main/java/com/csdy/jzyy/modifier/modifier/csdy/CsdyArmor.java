package com.csdy.jzyy.modifier.modifier.csdy;

import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CsdyArmor extends NoLevelsModifier implements EquipmentChangeModifierHook {
    @Override
    public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!(context.getChangedSlot().isArmor())) return;
        CoreMsUtil.setCategory(context.getEntity(), EntityCategory.csdy);
    }

    @Override
    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!(context.getChangedSlot().isArmor())) return;
        CoreMsUtil.setCategory(context.getEntity(), EntityCategory.normal);
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }
}
