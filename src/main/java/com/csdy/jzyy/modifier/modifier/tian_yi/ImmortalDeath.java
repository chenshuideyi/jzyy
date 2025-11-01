package com.csdy.jzyy.modifier.modifier.tian_yi;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.OnHoldingPreventDeathHook;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ImmortalDeath extends NoLevelsModifier implements OnHoldingPreventDeathHook {

    @Override
    public float onHoldingPreventDeath(LivingEntity living, IToolStackView tool, ModifierEntry entry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource) {
        return living.getMaxHealth();
    }
    @Override
    public boolean canIgnorePassInvul() {
        return true;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.PREVENT_DEATH);
    }
}
