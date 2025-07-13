package com.csdy.jzyy.modifier.modifier.warframe1999.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.csdyAABB;

public class Nyx extends NoLevelsModifier implements InventoryTickModifierHook {
    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (holder.tickCount % 100 != 0) return;
        if (!world.isClientSide()) { // 只在服务端执行

            List<LivingEntity> nearbyEntities = world.getEntitiesOfClass(LivingEntity.class, csdyAABB(10,holder));

            // 移除持有者自己，避免自残
            nearbyEntities.remove(holder);

            if (nearbyEntities.size() < 2) return; // 至少需要2个生物才能互相攻击

            // 让每个生物攻击最近的另一个生物
            for (LivingEntity entity : nearbyEntities) {
                LivingEntity closestTarget = null;
                double closestDistance = Double.MAX_VALUE;

                // 寻找最近的生物作为攻击目标
                for (LivingEntity potentialTarget : nearbyEntities) {
                    if (entity == potentialTarget) continue; // 不能攻击自己

                    double distance = entity.distanceToSqr(potentialTarget);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestTarget = potentialTarget;
                    }
                }

                // 强制攻击目标
                if (closestTarget != null) {
                    entity.setLastHurtByMob(closestTarget); // 设置攻击者（用于AI判断）
                    if (entity instanceof Mob mob) {
                        mob.setTarget(closestTarget); // 如果是Mob，直接设置目标
                    }
                }
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
