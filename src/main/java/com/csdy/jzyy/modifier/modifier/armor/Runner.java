package com.csdy.jzyy.modifier.modifier.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class Runner extends NoLevelsModifier implements AttributesModifierHook, EquipmentChangeModifierHook {

    private static final String SPEED_MODIFIER_NAME = "runner_speed";
    private final Map<UUID, EquipmentSlot> equippedPlayers = new HashMap<>();

    public Runner() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            UUID uuid = UUID.nameUUIDFromBytes((this.getClass().getSimpleName() + "_speed_" + slot.getName()).getBytes());
            AttributeModifier speedModifier = new AttributeModifier(
                    uuid,
                    SPEED_MODIFIER_NAME,
                    0.0,
                    AttributeModifier.Operation.MULTIPLY_BASE
            );
            consumer.accept(Attributes.MOVEMENT_SPEED, speedModifier);
        }
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player) {
            equippedPlayers.put(player.getUUID(), context.getChangedSlot());
            // 立即更新一次速度加成
            updateSpeedBonus(player, context.getChangedSlot());
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player) {
            equippedPlayers.remove(player.getUUID());
            UUID uuid = UUID.nameUUIDFromBytes((this.getClass().getSimpleName() + "_speed_" + context.getChangedSlot().getName()).getBytes());
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
        }
    }

    // 监听玩家tick事件来持续更新速度加成
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && equippedPlayers.containsKey(event.player.getUUID())) {
            EquipmentSlot slot = equippedPlayers.get(event.player.getUUID());
            updateSpeedBonus(event.player, slot);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateSpeedBonus(Player player, EquipmentSlot slot) {
        if (player.level().isClientSide()) {
            float fov = getPlayerFOV(player);
            float speedBonus = calculateSpeedBonusFromFOV(fov);

            UUID uuid = UUID.nameUUIDFromBytes((this.getClass().getSimpleName() + "_speed_" + slot.getName()).getBytes());

            // 移除旧的修饰符
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);

            // 添加新的修饰符
            AttributeModifier speedModifier = new AttributeModifier(
                    uuid,
                    SPEED_MODIFIER_NAME,
                    (double) speedBonus,
                    AttributeModifier.Operation.MULTIPLY_BASE
            );

            player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(speedModifier);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private float getPlayerFOV(Player player) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options != null) {
                // 获取基础FOV设置
                return (float) mc.options.fov().get();
            }
        } catch (Exception e) {
            // 如果获取失败，返回默认值
        }
        return 70.0f; // 默认FOV
    }

    private float calculateSpeedBonusFromFOV(float fov) {
        // 计算速度加成：100点FOV对应100%速度加成
        // 假设默认FOV为70，100点FOV对应1.0倍速度加成
        float defaultFov = 70.0f;
        float speedBonus = (fov - defaultFov) / 10.0f; // 每增加10点FOV，增加100%速度

        // 限制最大和最小值
        return Math.max(0.0f, Math.min(speedBonus, 5.0f)); // 限制在0%到200%之间
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }
}
