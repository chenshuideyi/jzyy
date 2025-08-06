package com.csdy.jzyy.modifier.modifier.dx.diadema;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.OnDeathModifierHook;
import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import com.csdy.tcondiadema.diadema.DiademaRegister;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.modifier.DiademaModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class AwakenPandora extends DiademaModifier implements OnDeathModifierHook, RequirementsModifierHook, ValidateModifierHook {

    @Override
    protected DiademaType getDiademaType() {
        return DiademaRegister.KILL_AURA.get();
    }

    @Override
    public void onDeathed(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource source, LivingEntity living, boolean isAliveSource) {
        if (living.hasEffect(JzyyEffectRegister.TRUE_MAN_LAST_DANCE.get())) return;
        living.setHealth(living.getMaxHealth()/2);
        living.deathTime = -2;
        living.isAlive();
        living.invulnerableTime = 60;
        living.addEffect(new MobEffectInstance(JzyyEffectRegister.TRUE_MAN_LAST_DANCE.get(),400,1));
    }

    @Override
    public @org.jetbrains.annotations.Nullable Component validate(IToolStackView tool, ModifierEntry entry) {
        if (tool.getModifierLevel(ModifierRegister.PANDORA.getId()) > 0) return null;
        return requirementsError(entry);
    }

    @Nullable
    @Override
    public Component requirementsError(ModifierEntry entry) {
        return Component.translatable("modifier.jzyy.awaken_pandora.requirements") ;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.ON_DEATH);
        hookBuilder.addHook(this, ModifierHooks.REQUIREMENTS);
        hookBuilder.addHook(this, ModifierHooks.VALIDATE);
        super.registerHooks(hookBuilder);
    }


}
