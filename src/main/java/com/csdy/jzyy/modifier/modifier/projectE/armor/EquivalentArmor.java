package com.csdy.jzyy.modifier.modifier.projectE.armor;

import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class EquivalentArmor extends NoLevelsModifier implements DamageBlockModifierHook {


    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        if (!(context.getEntity() instanceof ServerPlayer player)) {
            return false;
        }

        AtomicBoolean blocked = new AtomicBoolean(false);

        player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(knowledge -> {
            BigInteger EMC = knowledge.getEmc();
            BigInteger cost = BigInteger.valueOf((long)(damage * 2000));

            if (EMC.compareTo(cost) >= 0) {
                player.displayClientMessage(Component.literal("消耗了emc"+cost), false);
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
