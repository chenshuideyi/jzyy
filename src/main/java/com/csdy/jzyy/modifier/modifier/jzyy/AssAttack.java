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

public class AssAttack extends NoLevelsModifier implements MeleeHitModifierHook {

    private static final float DASH_SPEED = 2.0f;

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        if (context.getLivingTarget() == null || context.getPlayerAttacker() == null) return;
        initiateDash(context.getPlayerAttacker(), context.getLivingTarget());
//        forceLookAtBack(context.getPlayerAttacker(), context.getLivingTarget());
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    private void initiateDash(Player player, LivingEntity target) {
        // 计算目标背后的位置
        Vec3 targetLook = target.getLookAngle();
        Vec3 dashTargetPos = target.position()
                .add(targetLook.reverse().scale(1.5))
                .add(0, 0, 0); // 不调整高度

        // 计算冲刺方向向量
        Vec3 dashDirection = new Vec3(
                dashTargetPos.x - player.getX(),
                0, // 保持水平冲刺
                dashTargetPos.z - player.getZ()
        ).normalize();

        // 设置玩家冲刺状态
        player.setDeltaMovement(dashDirection.scale(DASH_SPEED));
        player.hurtMarked = true; // 强制同步运动数据,很重要

        // 让玩家面向目标
        player.setYRot(target.getYRot() + 180);
        player.setXRot(0);
        player.yRotO = player.getYRot();

    }

//    private void forceLookAtBack(Player player, LivingEntity target) {
//        // 1. 获取目标的背部位置（目标当前位置 + 目标视线反方向）
//        Vec3 targetLook = target.getLookAngle();
//        Vec3 backPosition = target.position()
//                .add(targetLook.reverse().scale(0.5)) // 稍微向背部偏移
//                .add(0, target.getBbHeight() * 0.5, 0); // 目标腰部高度
//
//        // 2. 计算从玩家到目标背部的方向向量
//        Vec3 directionToBack = backPosition.subtract(player.position()).normalize();
//
//        // 3. 计算精确的旋转角度
//        float yaw = (float)Math.toDegrees(Math.atan2(directionToBack.z, directionToBack.x)) - 90F;
//        float pitch = (float)-Math.toDegrees(Math.asin(directionToBack.y));
//
//        // 4. 设置玩家旋转（略微向上看以看到整个背部）
//        pitch = Math.min(pitch, -10); // 确保视角略微上抬
//
//        // 5. 应用旋转
//        player.setYRot(yaw);
//        player.setXRot(pitch);
//        player.yRotO = yaw;
//        player.xRotO = pitch;
//
//        // 6. 同步到客户端
//        if (player instanceof ServerPlayer serverPlayer) {
//            // 方法1：teleport包
//            serverPlayer.connection.teleport(
//                    player.getX(),
//                    player.getY(),
//                    player.getZ(),
//                    yaw,
//                    pitch,
//                    Collections.emptySet());
//
//            // 方法2：自定义网络包
////            PacketHandler.sendToClient(new SyncViewPacket(yaw, pitch), serverPlayer);
//        }
//    }
}
