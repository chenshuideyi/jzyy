package com.csdy.jzyy.modifier.modifier.hallowed_bar;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.HolyProtectionParticlePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class HolyProtection extends NoLevelsModifier implements DamageBlockModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        // 获取实体（玩家）
        LivingEntity entity = context.getEntity();

        // 检查实体是否没有冷却效果
        if (!entity.hasEffect(JzyyEffectRegister.HOLY_PROTECTION_COOLDOWN.get())) {
            // 添加冷却效果
            entity.addEffect(new MobEffectInstance(JzyyEffectRegister.HOLY_PROTECTION_COOLDOWN.get(), 600, 0));

            // **核心改动**：当闪避成功时，发送网络包以播放粒子效果
            if (!entity.level().isClientSide && entity instanceof ServerPlayer serverPlayer) {
                // 在服务端，向所有追踪该玩家的客户端发送粒子包
                JzyySyncing.CHANNEL.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer),
                        new HolyProtectionParticlePacket(serverPlayer.position())
                );
            }

            // 返回 true，表示伤害被完全格挡
            return true;
        }

        // 如果有冷却效果，返回 false，不格挡伤害
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
    }
}
