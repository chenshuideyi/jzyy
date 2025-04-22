package com.csdy.jzyy.modifier.modifier.jzyy;

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

public class HornToss extends NoLevelsModifier implements MeleeHitModifierHook {

    private static final float DASH_SPEED = 2.0f;

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        if (context.getLivingTarget() == null || context.getPlayerAttacker() == null) return;
        vibrateSpoon(context.getPlayerAttacker(), context.getLivingTarget());
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    private void vibrateSpoon(Player player, LivingEntity target) {
        // 1. 计算玩家背后的位置（目标落点）
        Vec3 playerLook = player.getLookAngle();
        Vec3 throwTargetPos = player.position()
                .add(playerLook.reverse().scale(1.5)) // 背后1.5格距离
                .add(0, 1.0, 0); // 初始高度抬升（避免落地太快）

        // 2. 计算抛射方向（带高抛物线）
        Vec3 throwDirection = new Vec3(
                throwTargetPos.x - target.getX(),
                (throwTargetPos.y - target.getY()) * 2.0, // Y轴速度加倍
                throwTargetPos.z - target.getZ()
        ).normalize();

        // 3. 施加高抛效果
        float throwStrength = 1.0f; // 抛射力度（越大飞越远）
        float upwardBoost = 2.5f;   // 额外向上的力（控制高度）

        target.setDeltaMovement(
                throwDirection.x * throwStrength,
                throwDirection.y * throwStrength * upwardBoost, // 关键：增加高度
                throwDirection.z * throwStrength
        );

        target.setYRot(target.getYRot() + 360 * (player.getRandom().nextFloat() - 0.5f));
        //强制同步运动数据
        target.hurtMarked = true;


    }
}
