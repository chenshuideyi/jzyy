package com.csdy.jzyy.item;

//import com.mega.uom.util.entity.EntityActuallyHurt;
import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import com.csdy.jzyy.ms.reclass.CsdyPlayer;
import com.csdy.jzyy.ms.reclass.CsdySeverPlayer;
import com.csdy.jzyy.util.Helper;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.csdy.jzyy.util.LivingEntityUtil.*;
import static com.csdy.jzyy.util.MsUtil.backTrack;
import static com.csdy.jzyy.util.MsUtil.superKillEntity;

public class Test extends Item {

    public Test() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.EPIC));
    }

    public static void spawnWaterSplashParticles(Level level, Vec3 position, int count) {
        for (int i = 0; i < count; i++) {
            level.addParticle(ParticleTypes.SPLASH,
                    position.x + (level.random.nextDouble() - 0.5) * 0.5,
                    position.y + 1,
                    position.z + (level.random.nextDouble() - 0.5) * 0.5,
                    (level.random.nextDouble() - 0.5) * 1.2,
                    level.random.nextDouble() * 0.3,
                    (level.random.nextDouble() - 0.5) * 1.2
            );

            if (i % 3 == 0) {
                level.addParticle(ParticleTypes.RAIN,
                        position.x + (level.random.nextDouble() - 0.5) * 0.8,
                        position.y + 0.5,
                        position.z + (level.random.nextDouble() - 0.5) * 0.8,
                        0, -0.1, 0
                );
            }
        }
    }

    public static void spawnExaggeratedWaterSplash(Level level, Vec3 position, int count) {
        RandomSource random = level.random;

        int splashCount = count * 2;
        int rainCount = count;

        for (int i = 0; i < splashCount; i++) {
            level.addParticle(
                    ParticleTypes.SPLASH,
                    position.x + (random.nextDouble() - 0.5) * 1.2,
                    position.y + 0.1,
                    position.z + (random.nextDouble() - 0.5) * 1.2,
                    (random.nextDouble() - 0.5) * 2.0,
                    random.nextDouble() * 1.5,
                    (random.nextDouble() - 0.5) * 2.0
            );
        }


        for (int i = 0; i < rainCount; i++) {
            level.addParticle(
                    ParticleTypes.RAIN,
                    position.x + (random.nextDouble() - 0.5) * 1.5,
                    position.y + 0.3,
                    position.z + (random.nextDouble() - 0.5) * 1.5,
                    (random.nextDouble() - 0.5) * 0.8,
                    random.nextDouble() * 1.8,
                    (random.nextDouble() - 0.5) * 0.8
            );
        }

        for (int i = 0; i < count / 2; i++) {
            level.addParticle(
                    ParticleTypes.FALLING_WATER,
                    position.x + (random.nextDouble() - 0.5) * 2.0,
                    position.y + random.nextDouble() * 2.0,
                    position.z + (random.nextDouble() - 0.5) * 2.0,
                    (random.nextDouble() - 0.5) * 1.5,
                    -0.2 - random.nextDouble() * 0.5,
                    (random.nextDouble() - 0.5) * 1.5
            );
        }

        for (int i = 0; i < count / 3; i++) {
            level.addParticle(
                    ParticleTypes.RAIN,
                    position.x + (random.nextDouble() - 0.5) * 1.0,
                    position.y + 0.5,
                    position.z + (random.nextDouble() - 0.5) * 1.0,
                    (random.nextDouble() - 0.5) * 0.3,
                    random.nextDouble() * 0.2,
                    (random.nextDouble() - 0.5) * 0.3
            );
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        if (target instanceof LivingEntity living) {
            spawnExaggeratedWaterSplash(living.level,living.position,1000);
            CoreMsUtil.setCategory(living, EntityCategory.csdykill);
//            superKillEntity(living);
//            backTrack(living.getClass());
//            EntityUntil.setCategory(living, EntityCategory.csdykill);
//            DamageSource src = new DamageSource(
//                    player.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
//                            .getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)
//            );
//            living.hurt(src ,10000);
//            new EntityActuallyHurt(living).actuallyHurt(living.level().damageSources().generic(), Float.POSITIVE_INFINITY, true);
//            test(player,living);
//            forceSetAllCandidateHealth(living,player,0);
        }
        return false;
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

//    @Override
//    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
//        if (entity instanceof Player player && !entity.level().isClientSide) {
//                double range = 5;
//                AABB attackBox = player.getBoundingBox().inflate(range);
//                List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, attackBox, e -> e != player);
//                for (LivingEntity target : targets) {
//                    // 判断是否为友军或目标免疫伤害
//                    if (target instanceof Player) continue;
//                    KillEntity(target);
//                }
//            }
//        return super.onEntitySwing(stack, entity);
//    }

    public static void KillEntity(Entity target) {
        if (target != null && !(target instanceof Player)) {
            MinecraftForge.EVENT_BUS.unregister(target);

            EntityInLevelCallback inLevelCallback = EntityInLevelCallback.NULL;
            target.levelCallback = inLevelCallback;
            target.setLevelCallback(inLevelCallback);
            target.getPassengers().forEach(Entity::stopRiding);
            Entity.RemovalReason reason = Entity.RemovalReason.KILLED;
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for (String s : living.getTags()) {
                    living.removeTag(s);
                }
                living.invalidateCaps();
                forceSetAllCandidateHealth(living,0);
            }


        }

    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);
        if (player instanceof LocalPlayer) Helper.replaceClass(player, CsdyPlayer.class);
        if (player instanceof ServerPlayer serverPlayer) Helper.replaceClass(serverPlayer, CsdySeverPlayer.class);
        return use;
    }

}
