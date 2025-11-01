package com.csdy.jzyy.modifier.modifier.tian_yi;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowHitModifierHook;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.List;

public class TheCenterOfThePeculiarStorm extends NoLevelsModifier implements ArrowHitModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
        // 只在服务端执行
        if (arrow.level().isClientSide) return;

        Level level = arrow.level();
        float pullRadius = 16.0f; // 吸引半径
        float pullStrength = 16f; // 吸引强度

        // 查找附近的实体
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(target.blockPosition()).inflate(pullRadius),
                entity -> entity != target &&
                        entity != attacker &&
                        entity.isAlive()
        );

        // 吸引所有附近的实体
        for (LivingEntity entity : nearbyEntities) {
            pullEntityToTarget(entity, target, pullStrength);
        }
    }

    /**
     * 将实体拉向目标
     */
    private void pullEntityToTarget(LivingEntity entity, LivingEntity target, float strength) {
        // 计算从实体指向目标的方向向量
        double dx = target.getX() - entity.getX();
        double dy = target.getY() - entity.getY();
        double dz = target.getZ() - entity.getZ();

        // 计算距离
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // 标准化方向向量并应用强度
        if (distance > 0) {
            double power = strength / distance; // 距离越近拉力越强

            // 设置实体的运动速度
            entity.setDeltaMovement(
                    entity.getDeltaMovement().x + dx * power * 0.3,
                    entity.getDeltaMovement().y + dy * power * 0.3 + 0.2, // 稍微向上拉
                    entity.getDeltaMovement().z + dz * power * 0.3
            );

            // 确保实体不会卡在空中
            entity.hurtMarked = true;
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_HIT);
    }
}
