package com.csdy.jzyy.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.gameevent.GameEvent;

import static com.csdy.jzyy.method.LivingEntityUtil.*;

public class Test extends Item {
    public Test() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.EPIC));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        if (target instanceof LivingEntity living) {
            reflectionSetMaxHealth(living,player,1);
//            test(player,living);
//            forceSetAllCandidateHealth(living,player,0);
        }
        return true; // 取消原版伤害
    }

    private void test(Player player ,Entity targetEntity) {
        if (targetEntity instanceof LivingEntity living && living.level() instanceof ServerLevel serverLevel && living.isAlive()) {
            forceSetAllCandidateHealth(living,0);
            living.getBrain().clearMemories();
//            player.killedEntity(serverLevel, living);
            serverLevel.broadcastEntityEvent(living, (byte) 3);
            for (int loli = 0; loli < 20; loli++) {
                int finalLoli = loli;
                Thread thread = new Thread(() -> {
                    try {
                        while (Minecraft.getInstance().isRunning()) {
                            if (finalLoli == 19) {
                                living.remove(Entity.RemovalReason.KILLED);
                                serverLevel.entityTickList.remove(living);
                                addDeathParticle(serverLevel, living.getX(), living.getY(), living.getZ());
                                break;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                });
                thread.start();
                thread.stop();
            }
        }
    }

    public static void addDeathParticle(ServerLevel serverLevel, double x, double y, double z) {
        for (int i = 0; i<14; i++) {
            SimpleParticleType type = ParticleTypes.BUBBLE.getType();
            serverLevel.addParticle(type, x, y, z, 0, 0, 0);
        }
    }
}
