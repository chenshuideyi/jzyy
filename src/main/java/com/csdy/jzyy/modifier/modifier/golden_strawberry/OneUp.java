package com.csdy.jzyy.modifier.modifier.golden_strawberry;

import com.csdy.jzyy.item.register.ItemRegister;
import com.csdy.jzyy.modifier.util.JzyyAnimationHandler;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.network.packets.UndyingAnimationPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.WeakHashMap;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OneUp extends NoLevelsModifier implements MeleeHitModifierHook {

    private static final WeakHashMap<Player, Integer> killCountMap = new WeakHashMap<>();

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();

        // 只在服务端处理且目标存在
        if (target != null && player != null && !target.level().isClientSide) {
            // 检查目标是否被这次攻击杀死
            if (target.getHealth() <= 0 && !target.isAlive()) {
                // 增加击杀计数
                int newCount = killCountMap.getOrDefault(player, 0) + 1;
                killCountMap.put(player, Math.min(newCount, 8)); // 上限8

                // 如果达到8点，给玩家复活能力
                if (newCount >= 8) {
                    player.displayClientMessage(Component.literal("已获得复活能力！死亡后将自动复活"), true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            Integer count = killCountMap.get(player);
            if (count != null && count >= 8) {
                // 取消死亡事件
                event.setCanceled(true);

                player.setHealth(player.getMaxHealth() * 0.5F);
                player.removeAllEffects();

                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        100,
                        1,
                        false,
                        true
                ));

                player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        100,
                        1,
                        false,
                        true
                ));

                if (!player.level().isClientSide) {
                    playCustomTotemEffect(player, ItemRegister.GOLDEN_STRAWBERRY.get().getDefaultInstance());
                }

                // 重置计数器
                killCountMap.put(player, 0);

            }
        }
    }

    public static void playCustomTotemEffect(Player player, ItemStack customTotemItem) {
        if (player.level() instanceof ServerLevel serverLevel) {
            // 1. 播放标准的不死图腾音效和粒子 (这部分代码保持不变)
            serverLevel.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TOTEM_USE,
                    SoundSource.PLAYERS,
                    1.0F, 1.0F
            );

            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                    100,
                    0.5, 0.5, 0.5,
                    0.2
            );


            JzyySyncing.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player),
                    new UndyingAnimationPacket(customTotemItem)
            );

        }

    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    // 获取玩家当前击杀点数的方法（可用于显示在HUD等）
    public static int getKillCount(Player player) {
        return killCountMap.getOrDefault(player, 0);
    }

}
