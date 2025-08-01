package com.csdy.jzyy.modifier.modifier.jzyy.sponsor;

import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Ciallo extends NoLevelsModifier implements OnAttackedModifierHook , MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        if (target != null && !target.level().isClientSide && attacker.level instanceof ServerLevel serverLevel) {
            if (attacker instanceof Player player) {
                sayCiallo(serverLevel, player);
            }
        }
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;
        if (player.level instanceof ServerLevel serverLevel) {
            sayCiallo(serverLevel, player);
        }
    }

    private static void sayCiallo(ServerLevel level, Player player){
        for (ServerPlayer serverPlayer : level.players()) {
            if (!serverPlayer.level().isClientSide()) {
                JzyySyncing.CHANNEL.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new PlaySoundPacket(player.getEyePosition(), JzyySoundsRegister.CIALLO.get().getLocation(), 1, 1)
                );
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }


}
