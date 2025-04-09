package com.csdy.jzyy.modifier.modifier.yue_zheng_ling;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Cumulonimbus extends NoLevelsModifier implements EquipmentChangeModifierHook {

    @Override
    public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)) return;
        setRain(serverLevel);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }


    public static void setRain(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        //好几把麻烦
        serverLevel.setWeatherParameters(
                0, // clearTime
                412 * 20,  // ticks
                true,      // 下雨
                false      // 雷暴
        );
    }


}
