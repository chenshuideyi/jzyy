package com.csdy.jzyy.modifier.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class MaliciouslySlandering extends NoLevelsModifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();

        if (target != null && attacker != null) {
            // 获取目标附近的生物
            List<LivingEntity> nearbyEntities = target.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(8),
                    entity -> entity instanceof Mob && entity != target && entity != attacker
            );

            // 让附近的生物仇恨攻击者
            for (LivingEntity entity : nearbyEntities) {
                if (entity instanceof Mob mob) {
                    provokeMob(mob, attacker);
                }
            }
        }
    }

    private void provokeMob(Mob mob, LivingEntity attacker) {
        mob.setLastHurtByMob(attacker);
        mob.setTarget(attacker);

        if (attacker instanceof Player player) {
            mob.setLastHurtByPlayer(player);
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
