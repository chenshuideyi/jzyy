package com.csdy.jzyy.modifier.modifier.eva;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class Longinus extends Modifier implements MeleeDamageModifierHook, ArrowDamageModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        int level = entry.getLevel();
        float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
        float amount = 100 * toolDamage * (level * 2 - 1);
        if (damage * amount < 2147483647) return damage * amount;
        return damage;
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        if (target instanceof LivingEntity) {
            int level = entry.getLevel();
            float arrowDamage = (float) arrow.getBaseDamage();
            float amount = 1000 * arrowDamage * (level * 2 - 1);
            return damage * amount;
        }
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }


}
