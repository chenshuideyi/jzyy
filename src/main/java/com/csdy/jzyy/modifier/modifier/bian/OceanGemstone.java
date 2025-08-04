package com.csdy.jzyy.modifier.modifier.bian;

import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class OceanGemstone {

    public static class sourceofmagic extends Modifier implements MeleeDamageModifierHook {


        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this,ModifierHooks.MELEE_DAMAGE);
        }

        @Override
        public float getMeleeDamage(@NotNull IToolStackView tool, ModifierEntry modifierEntry, @NotNull ToolAttackContext context, float baseDamage, float damage) {
            int level = modifierEntry.getLevel();
            DamageSource damageSource = getDamageSource(context);


            if (damageSource != null && isMagicDamage(damageSource)) {
                return damage * (1 + 0.2f * level);
            }
            return damage;
        }


        private boolean isMagicDamage(DamageSource source) {
            Holder<DamageType> damageType = source.typeHolder();
            return damageType.is(DamageTypeTags.BYPASSES_ARMOR) ||
                    damageType.is(DamageTypeTags.BYPASSES_RESISTANCE) ||
                    damageType.is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        }


        private DamageSource getDamageSource(ToolAttackContext context) {
            LivingEntity attacker = context.getAttacker();
            LivingEntity target = context.getLivingTarget();
            if (attacker instanceof Player player && target != null) {
                return attacker.damageSources().playerAttack(player);
            }
            return null;
        }
    }
}