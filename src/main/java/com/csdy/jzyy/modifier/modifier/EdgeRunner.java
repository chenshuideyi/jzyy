package com.csdy.jzyy.modifier.modifier;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import static java.lang.Math.pow;


public class EdgeRunner extends NoLevelsModifier implements MeleeDamageModifierHook, ArrowDamageModifierHook {

    Runtime rt = Runtime.getRuntime();
    final int byteToMb = 1024 * 1024;

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        long vmMax = rt.maxMemory()/byteToMb;
        float value = (float) pow(2, (double) (4096 - vmMax) / (256));
        return damage * value;
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        if (attacker instanceof Player && target instanceof LivingEntity) {
            long vmMax = rt.maxMemory()/byteToMb;
            float value = (float) pow(2, (double) (4096 - vmMax) / (256));
            return damage * value;
        }
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }


}
