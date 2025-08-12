package com.csdy.jzyy.modifier.modifier.warframe1999.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.csdyAABB;

public class Mag extends NoLevelsModifier implements InventoryTickModifierHook {
    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide()) { // 只在服务端执行
            // 获取持有者周围一定范围内的所有实体

            List<Entity> entities = world.getEntities(null, csdyAABB(5,holder));

            for (Entity entity : entities) {
                if (entity != holder && entity.isAlive() && entity instanceof ItemEntity) { // 排除持有者自身且只对活着的实体生效
                    // 计算从实体指向持有者的方向向量
                    Vec3 direction = new Vec3(holder.getX() - entity.getX(),
                            holder.getY() - entity.getY(),
                            holder.getZ() - entity.getZ()).normalize();

                    // 设置一个适中的吸引力强度
                    double strength = 0.1 * modifier.getLevel(); // 根据修饰符等级调整强度

                    // 应用速度改变
                    entity.setDeltaMovement(entity.getDeltaMovement().add(direction.scale(strength)));

                    // 可选：防止实体速度过快
                    Vec3 currentMotion = entity.getDeltaMovement();
                    double maxSpeed = 0.5;
                    if (currentMotion.length() > maxSpeed) {
                        entity.setDeltaMovement(currentMotion.normalize().scale(maxSpeed));
                    }

                    // 标记实体已经移动
                    entity.hurtMarked = true;
                }
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
