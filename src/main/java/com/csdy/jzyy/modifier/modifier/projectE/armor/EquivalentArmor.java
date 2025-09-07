package com.csdy.jzyy.modifier.modifier.projectE.armor;

import com.google.common.collect.Maps;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.levelOfEMCArmor;

public class EquivalentArmor extends Modifier implements DamageBlockModifierHook {


    // 存储玩家冷却时间的Map，键为玩家UUID，值为冷却结束的时间戳
    private static final Map<UUID, Long> cooldownMap = Maps.newConcurrentMap();
    // 超载阈值
    private static final float OVERLOAD_THRESHOLD = 1000.0f;
    // 冷却时间（毫秒）
    private static final long COOLDOWN_DURATION = 1000L;

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        if (!(context.getEntity() instanceof ServerPlayer player)) {
            return false;
        }

        // 检查冷却状态
        UUID playerId = player.getUUID();
        long currentTime = System.currentTimeMillis();
        if (cooldownMap.containsKey(playerId)) {
            long cooldownEnd = cooldownMap.get(playerId);
            if (currentTime < cooldownEnd) {
//                // 处于冷却中，不提供防御
//                player.displayClientMessage(Component.literal("护盾冷却中，无法防御!"), false);
                return false;
            } else {
                // 冷却结束，移除记录
                cooldownMap.remove(playerId);
            }
        }

        AtomicBoolean blocked = new AtomicBoolean(false);

        player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(knowledge -> {
            BigInteger EMC = knowledge.getEmc();
            BigInteger cost = BigInteger.valueOf((long)(damage * 2000));

            if (EMC.compareTo(cost) >= 0) {
                if (damage >= OVERLOAD_THRESHOLD * levelOfEMCArmor(player)) {
                    cooldownMap.put(playerId, currentTime + COOLDOWN_DURATION);
                    player.displayClientMessage(Component.literal("检测到途中巨大打击，emc护盾正在重载！"), true);
                }

                player.displayClientMessage(Component.literal("消耗了EMC: " + cost), false);
                knowledge.setEmc(EMC.subtract(cost));
                blocked.set(true);
            }
        });

        return blocked.get();
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
    }

}
