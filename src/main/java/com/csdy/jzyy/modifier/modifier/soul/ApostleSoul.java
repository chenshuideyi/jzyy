package com.csdy.jzyy.modifier.modifier.soul;

import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ApostleSoul extends NoLevelsModifier implements OnAttackedModifierHook {

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;
        spawnUnholyBloodItem(player);
    }

    private void spawnUnholyBloodItem(Player player) {
        Level level = player.level;

        // 获取UNHOLY_BLOOD物品堆栈
        ItemStack unholyBloodStack = ModItems.UNHOLY_BLOOD.get().getDefaultInstance();

        // 设置随机数量（如果需要）
        unholyBloodStack.setCount(1); // 或者随机数量：player.getRandom().nextInt(3) + 1

        // 在玩家周围随机位置生成物品实体
        double offsetX = (player.getRandom().nextDouble() - 0.5) * 2.0;
        double offsetY = player.getRandom().nextDouble() * 1.5;
        double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2.0;

        double posX = player.getX() + offsetX;
        double posY = player.getY() + offsetY;
        double posZ = player.getZ() + offsetZ;

        // 创建物品实体
        ItemEntity itemEntity = new ItemEntity(level, posX, posY, posZ, unholyBloodStack);

        // 设置一些物理效果
        itemEntity.setDeltaMovement(
                (player.getRandom().nextDouble() - 0.5) * 0.2,
                player.getRandom().nextDouble() * 0.2 + 0.1,
                (player.getRandom().nextDouble() - 0.5) * 0.2
        );

        itemEntity.setPickUpDelay(20); // 1秒后才能拾取
        itemEntity.setThrower(player.getUUID()); // 设置抛出者

        // 添加到世界
        level.addFreshEntity(itemEntity);

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }
}
