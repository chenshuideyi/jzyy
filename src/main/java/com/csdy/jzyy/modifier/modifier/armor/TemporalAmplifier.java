package com.csdy.jzyy.modifier.modifier.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class TemporalAmplifier extends NoLevelsModifier implements AttributesModifierHook, EquipmentChangeModifierHook {

    private static final String SPEED_MODIFIER_NAME = "temporal_speed";
    private static final String ATTACK_MODIFIER_NAME = "temporal_attack";
    private static final String ARMOR_MODIFIER_NAME = "temporal_armor";

    // 存储每个玩家的计时器、当前加成等级和装备slot
    private final Map<UUID, Long> equippedTimeMap = new HashMap<>();
    private final Map<UUID, Integer> currentBonusLevelMap = new HashMap<>();
    private final Map<UUID, EquipmentSlot> playerSlots = new HashMap<>();

    // 配置参数
    private static final int MAX_BONUS_LEVEL = 100;
    private static final int SECONDS_PER_LEVEL = 300;
    private static final double SPEED_BONUS_PER_LEVEL = 0.05;
    private static final double ATTACK_BONUS_PER_LEVEL = 0.5;
    private static final double ARMOR_BONUS_PER_LEVEL = 1.0;

    public TemporalAmplifier() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                    getSpeedUuid(slot), SPEED_MODIFIER_NAME, 0.0, AttributeModifier.Operation.MULTIPLY_BASE
            ));

            consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    getAttackUuid(slot), ATTACK_MODIFIER_NAME, 0.0, AttributeModifier.Operation.ADDITION
            ));

            consumer.accept(Attributes.ARMOR, new AttributeModifier(
                    getArmorUuid(slot), ARMOR_MODIFIER_NAME, 0.0, AttributeModifier.Operation.ADDITION
            ));
        }
    }

    // UUID生成辅助方法
    private UUID getSpeedUuid(EquipmentSlot slot) {
        return UUID.nameUUIDFromBytes((SPEED_MODIFIER_NAME + "_" + slot.getName()).getBytes());
    }

    private UUID getAttackUuid(EquipmentSlot slot) {
        return UUID.nameUUIDFromBytes((ATTACK_MODIFIER_NAME + "_" + slot.getName()).getBytes());
    }

    private UUID getArmorUuid(EquipmentSlot slot) {
        return UUID.nameUUIDFromBytes((ARMOR_MODIFIER_NAME + "_" + slot.getName()).getBytes());
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player) {
            UUID playerId = player.getUUID();
            EquipmentSlot slot = context.getChangedSlot();

            equippedTimeMap.put(playerId, player.level().getGameTime());
            currentBonusLevelMap.put(playerId, 0);
            playerSlots.put(playerId, slot);

            updateAttributes(player);
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player) {
            UUID playerId = player.getUUID();
            EquipmentSlot slot = context.getChangedSlot();

            equippedTimeMap.remove(playerId);
            currentBonusLevelMap.remove(playerId);
            playerSlots.remove(playerId);

            removeAllAttributes(player, slot);
        }
    }

    // 使用PlayerTickEvent而不是ServerTickEvent
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END &&
                event.player != null &&
                equippedTimeMap.containsKey(event.player.getUUID())) {

            if (event.player.isAlive()) {
                updateBonusLevel(event.player);
                updateAttributes(event.player);
            } else {
                // 玩家死亡，移除记录
                UUID playerId = event.player.getUUID();
                EquipmentSlot slot = playerSlots.get(playerId);
                if (slot != null) {
                    removeAllAttributes(event.player, slot);
                }
                equippedTimeMap.remove(playerId);
                currentBonusLevelMap.remove(playerId);
                playerSlots.remove(playerId);
            }
        }
    }

    // 添加玩家退出事件处理
    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
            var player = event.getEntity();
            UUID playerId = player.getUUID();
            EquipmentSlot slot = playerSlots.get(playerId);
            if (slot != null) {
                removeAllAttributes(player, slot);
            }
            equippedTimeMap.remove(playerId);
            currentBonusLevelMap.remove(playerId);
            playerSlots.remove(playerId);

    }

    private void updateBonusLevel(Player player) {
        UUID playerId = player.getUUID();
        Long equippedTime = equippedTimeMap.get(playerId);
        if (equippedTime == null) return;

        long currentTime = player.level().getGameTime();
        long elapsedTicks = currentTime - equippedTime;
        long elapsedSeconds = elapsedTicks / 20;

        int newLevel = (int) (elapsedSeconds / SECONDS_PER_LEVEL);
        newLevel = Math.min(newLevel, MAX_BONUS_LEVEL);

        Integer currentLevel = currentBonusLevelMap.get(playerId);
        if (currentLevel == null || newLevel != currentLevel) {
            currentBonusLevelMap.put(playerId, newLevel);
        }
        System.out.println("这里也触发了");
    }

    private void updateAttributes(Player player) {
        UUID playerId = player.getUUID();
        Integer bonusLevel = currentBonusLevelMap.get(playerId);
        EquipmentSlot slot = playerSlots.get(playerId);

        if (bonusLevel == null) bonusLevel = 0;
        if (slot == null) return;

        double speedBonus = bonusLevel * SPEED_BONUS_PER_LEVEL;
        double attackBonus = bonusLevel * ATTACK_BONUS_PER_LEVEL;
        double armorBonus = bonusLevel * ARMOR_BONUS_PER_LEVEL;

        removeAllAttributes(player, slot);

        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(
                getSpeedUuid(slot), SPEED_MODIFIER_NAME, speedBonus, AttributeModifier.Operation.MULTIPLY_BASE
        ));

        player.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(
                getAttackUuid(slot), ATTACK_MODIFIER_NAME, attackBonus, AttributeModifier.Operation.ADDITION
        ));

        player.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(
                getArmorUuid(slot), ARMOR_MODIFIER_NAME, armorBonus, AttributeModifier.Operation.ADDITION
        ));
        System.out.println("应该是生效了");
    }

    private void removeAllAttributes(Player player, EquipmentSlot slot) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(getSpeedUuid(slot));
        player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(getAttackUuid(slot));
        player.getAttribute(Attributes.ARMOR).removeModifier(getArmorUuid(slot));
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }
}
