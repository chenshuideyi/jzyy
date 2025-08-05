package com.csdy.jzyy.modifier.modifier.srp;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nonnull;

import static java.lang.Math.min;

public class Infested extends Modifier implements MeleeDamageModifierHook {
    @Override
    public float getMeleeDamage(@Nonnull IToolStackView tool, ModifierEntry modifierEntry, @Nonnull ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        int level = modifierEntry.getLevel();
        if (target != null && attacker instanceof Player player) {
            ToolDamageUtil.repair(tool,level*2);
            player.getFoodData().eat(3, (float)level * 0.5F);
            player.heal((min(0.05f*damage*level,2*level)));
        }
        return damage * 1.1f * level;
    }

}
