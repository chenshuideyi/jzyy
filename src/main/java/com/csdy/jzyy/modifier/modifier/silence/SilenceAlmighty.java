package com.csdy.jzyy.modifier.modifier.silence;

import com.c2h6s.etstlib.entity.specialDamageSources.PercentageBypassArmorSource;
import com.c2h6s.etstlib.register.EtSTLibHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.*;

public class SilenceAlmighty extends NoLevelsModifier implements ToolDamageModifierHook, VolatileDataModifierHook {

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry entry, ToolDataNBT volatileData) {
        for (SlotType slotType : SlotType.getAllSlotTypes()) {
            volatileData.addSlots(slotType, 8);
        }
    }

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry entry, int amount, @Nullable LivingEntity holder) {
        return 0;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA);
    }

}
