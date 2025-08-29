package com.csdy.jzyy.modifier.modifier.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Adaptation extends NoLevelsModifier implements DamageBlockModifierHook {

    private final Map<UUID, Float> lastDamageMap = new HashMap<>();

    public Adaptation() {
        // 注册到事件总线来监听伤害和死亡事件
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        LivingEntity entity = context.getEntity();
        UUID entityId = entity.getUUID();

        // 检查是否有上一次伤害记录
        if (lastDamageMap.containsKey(entityId)) {
            float lastDamage = lastDamageMap.get(entityId);
            // 只格挡比上一次伤害更低的伤害
            return damage < lastDamage;
        }

        // 如果没有上一次伤害记录，不格挡
        return false;
    }

    // 监听伤害事件，记录受到的伤害
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        float damage = event.getAmount();

        // 记录这次受到的伤害（用于下一次格挡判断）
        lastDamageMap.put(entity.getUUID(), damage);
    }

    // 监听死亡事件，重置记录
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        lastDamageMap.remove(entity.getUUID());
    }

    // 可选：监听玩家退出游戏事件，清理内存
    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        lastDamageMap.remove(event.getEntity().getUUID());
    }

    // 可选：监听玩家维度切换事件
    @SubscribeEvent
    public void onPlayerChangedDimension(net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        // 可以根据需要决定是否重置
        // lastDamageMap.remove(event.getEntity().getUUID());
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
    }
}
