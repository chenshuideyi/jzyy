package com.csdy.jzyy.modifier.modifier.ice_and_fire;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN;

public class DragonIce extends NoLevelsModifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            target.addEffect(new MobEffectInstance(MOVEMENT_SLOWDOWN,30,2));
            repelEntity(target,player);
        }

    }

    private void repelEntity(Entity entity,Player player) {
        // 获取玩家和实体的位置
        Vec3 playerPos = player.position();
        Vec3 entityPos = entity.position();


        // 计算排斥方向（从玩家指向实体）
        Vec3 repelDirection = entityPos.subtract(playerPos).normalize();

        // 设置排斥速度
        double maxRepelStrength = 3; // 排斥强度，可以调整
        Vec3 repelVelocity = repelDirection.scale(maxRepelStrength);

        // 为实体设置速度
        entity.setDeltaMovement(repelVelocity);

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
