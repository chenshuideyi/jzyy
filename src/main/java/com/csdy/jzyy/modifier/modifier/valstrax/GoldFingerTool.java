package com.csdy.jzyy.modifier.modifier.valstrax;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.CriticalAttackModifierHook;
import com.csdy.jzyy.modifier.register.JzyyModifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import static slimeknights.tconstruct.library.tools.stat.ToolStats.ARMOR;

public class GoldFingerTool extends JzyyModifier implements CriticalAttackModifierHook {

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public int onDamageTool(IToolStackView iToolStackView, ModifierEntry modifierEntry, int i, @Nullable LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public boolean setCritical(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, InteractionHand interactionHand, Entity entity, EquipmentSlot equipmentSlot, boolean b, boolean b1, boolean b2) {
        return true;
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder){
        ARMOR.multiply(builder, 1.5F);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, EtSTLibHooks.CRITICAL_ATTACK);
    }





}
