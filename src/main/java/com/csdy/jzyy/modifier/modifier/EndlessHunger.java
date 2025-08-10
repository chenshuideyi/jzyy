package com.csdy.jzyy.modifier.modifier;

import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EndlessHunger extends NoLevelsModifier implements MeleeDamageModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    @Override
    public float getMeleeDamage(@NotNull IToolStackView iToolStackView, @NotNull ModifierEntry modifierEntry, @NotNull ToolAttackContext toolAttackContext, float damage, float baseDamage) {
        LivingEntity target = toolAttackContext.getLivingTarget();
        var holder = toolAttackContext.getPlayerAttacker();
        if (target == null || holder == null) return damage;
        if (holder.getFoodData().getFoodLevel() <= 4){
            holder.getFoodData().eat(20, 20);
            return damage + 4000f;
        }

        return damage;
    }

}
