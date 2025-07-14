package com.csdy.jzyy.modifier.modifier.csdytinker;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Random;

public class CoupDeGrace extends Modifier implements MeleeDamageModifierHook {
    //恩赐解脱
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        if (target != null) {
            var level = entry.getLevel();
            Random random = new Random();
            int randomNum = random.nextInt(100);
            if (randomNum >= 78) {
                if (level > 3) {
                    level = 3;
                }
                return damage * ((level - 1) * 1.25f) + damage;
            }
        }
        return damage;
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        if (level >= 3) return getDisplayName(3);
        return super.getDisplayName(level);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }
}
