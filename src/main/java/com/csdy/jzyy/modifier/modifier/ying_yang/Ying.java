package com.csdy.jzyy.modifier.modifier.ying_yang;

import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

public class Ying extends NoLevelsModifier implements InventoryTickModifierHook {

    private static final ModifierId[] POSSIBLE_MODIFIERS = {
            ModifierRegister.MAG_STATIC_MODIFIER.getId(),
            ModifierRegister.NYX_STATIC_MODIFIER.getId(),
            ModifierRegister.TRINITY_STATIC_MODIFIER.getId(),
    };

    private boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        return false;
    }

    public void transformerRealForm(ToolStack tool,@Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!shouldRevealRealForm(tool, holder)) return;
        MaterialNBT materials = tool.getMaterials();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariant variant = materials.get(i);

        }
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        transformerRealForm((ToolStack) tool,holder);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }


}
