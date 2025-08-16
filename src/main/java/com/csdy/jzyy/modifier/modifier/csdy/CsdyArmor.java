package com.csdy.jzyy.modifier.modifier.csdy;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.PLZBase;
import com.csdy.jzyy.ms.enums.ClassOption;
import com.csdy.jzyy.ms.enums.EntityCategory;
import com.csdy.jzyy.ms.reclass.CsdyPlayer;
import com.csdy.jzyy.ms.reclass.CsdyServerPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        if (!context.getChangedSlot().isArmor()) {
            return;
        }

        var living = context.getEntity();
        if (!(living instanceof Player player)) {
            return;
        }

//        CoreMsUtil.setCategory(player, EntityCategory.csdy);

        if (!(player instanceof CsdyServerPlayer) && player instanceof ServerPlayer) {
            PLZBase.klassPtr(player, PLZBase.defineHiddenClassInPackage(null, JzyyModMain.class, "com.csdy.jzyy.ms.reclass.CsdyServerPlayer", null, ClassOption.STRONG));
        }
        if (!(player instanceof CsdyPlayer) && player instanceof LocalPlayer) {
            PLZBase.klassPtr(player,PLZBase.defineHiddenClassInPackage(null, JzyyModMain.class, "com.csdy.jzyy.ms.reclass.CsdyPlayer", null, ClassOption.STRONG));
        }
    }

    @Override
    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!context.getChangedSlot().isArmor()) {
            return;
        }

        var living = context.getEntity();
        if (!(living instanceof Player player)) {
            return;
        }

//        CoreMsUtil.setCategory(player, EntityCategory.normal);
    }



    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }
}
