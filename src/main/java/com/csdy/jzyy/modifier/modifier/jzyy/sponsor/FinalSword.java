package com.csdy.jzyy.modifier.modifier.jzyy.sponsor;

import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.stat.ToolStats.ATTACK_DAMAGE;

public class FinalSword extends NoLevelsModifier implements RequirementsModifierHook, ValidateModifierHook, ToolStatsModifierHook {

    @Override
    public @org.jetbrains.annotations.Nullable Component validate(IToolStackView tool, ModifierEntry entry) {
        if (tool.getModifierLevel(ModifierRegister.BLADE_RELEASE3_STATIC_MODIFIER.getId()) > 0) return null;
        return requirementsError(entry);
    }

    @Nullable
    @Override
    public Component requirementsError(ModifierEntry entry) {
        return Component.translatable("modifier.jzyy.final_sword.requirements") ;
    }


    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        ATTACK_DAMAGE.multiply(builder, 2);
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.REQUIREMENTS);
        hookBuilder.addHook(this, ModifierHooks.VALIDATE);
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
    }

}
