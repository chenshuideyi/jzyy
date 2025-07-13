package com.csdy.jzyy.modifier.modifier.tnt;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.SelfExplosion;

public class SelfExplosionArmor extends Modifier implements ToolDamageModifierHook, OnAttackedModifierHook {

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot equipmentSlot, DamageSource damageSource, float v, boolean b) {
        SelfExplosion(context.getEntity().level(), entry.getLevel(), context.getEntity());
    }

    @Override
    public int onDamageTool(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, int amount, @Nullable LivingEntity holder) {
        if (holder != null) {
            SelfExplosion(holder.level(),entry.getLevel(),holder);
            return amount * entry.getLevel();
        }
        return amount;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }
}
