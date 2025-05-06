package com.csdy.jzyy.modifier.modifier.real_form;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

///经验钢真实形态——完美
///我已达到顶点，而后只有衰败
public class RealExperience extends RealFormBaseModifier {

    public RealExperience(String materialId, MaterialVariantId reMaterialId) {
        super(materialId, reMaterialId);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) {
            return false;
        }
        return player.experienceLevel > 300;
    }

}
