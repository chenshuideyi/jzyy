package com.csdy.jzyy.modifier.modifier.command_block;

import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.awt.*;

public class Gamemode1 extends NoLevelsModifier implements EquipmentChangeModifierHook {

    @Override
    public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!context.getChangedSlot().isArmor()) {
            return;
        }

        var living = context.getEntity();
        if (living instanceof ServerPlayer serverplayer) {
            serverplayer.setGameMode(GameType.CREATIVE);
            serverplayer.displayClientMessage(Component.literal("已将自己的游戏模式切改为创造模式"), true);
        }
    }

    @Override
    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        if (!context.getChangedSlot().isArmor()) {
            return;
        }

        var living = context.getEntity();
        if (living instanceof ServerPlayer serverplayer) {
            serverplayer.setGameMode(GameType.SURVIVAL);
            serverplayer.displayClientMessage(Component.literal("已将自己的游戏模式切改为生存模式"), true);
        }

    }



    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }
}
