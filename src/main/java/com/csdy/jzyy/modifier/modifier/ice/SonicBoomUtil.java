package com.csdy.jzyy.modifier.modifier.ice;


import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.JzyySonicBoomPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class SonicBoomUtil {

    public static void performSonicBoom(Player player, float power) {
        if (player == null) return;
        Level level = player.level();

        // 计算瞄准方向
        HitResult hitResult = player.pick(20.0, 0.0F, false);
        Vec3 targetPos = hitResult.getType() == HitResult.Type.MISS
                ? player.getEyePosition().add(player.getLookAngle().scale(20))
                : hitResult.getLocation();

        Vec3 direction = targetPos.subtract(player.getEyePosition()).normalize();

        // 播放音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS,
                0.5F + power * 0.5F, 0.9F + player.getRandom().nextFloat() * 0.2F);

        // 发送网络包
        JzyySyncing.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new JzyySonicBoomPacket(player.getEyePosition(), direction, power)
        );

        // 服务端伤害逻辑
        if (!level.isClientSide) {
            double range = 15.0 * power;
            double damage = 10.0 * power;

            // 射线检测攻击实体
            AABB area = player.getBoundingBox()
                    .expandTowards(direction.scale(range))
                    .inflate(2.0, 2.0, 2.0);

            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
                if (target != player && player.hasLineOfSight(target)) {
                    Vec3 toTarget = target.getEyePosition().subtract(player.getEyePosition());
                    double angle = direction.dot(toTarget.normalize());

                    // 检查是否在扇形范围内
                    if (angle > 0.9) { // ~25度锥形范围

                        player.attack(target);

                        Vec3 knockbackDirection = target.position().subtract(target.position()).normalize();

                        if (target instanceof ServerPlayer serverPlayer) {
                            JzyySyncing.CHANNEL.send(
                                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                                    new JzyySonicBoomPacket(target.getEyePosition(), knockbackDirection, 0.5F)
                            );
                        }
                    }
                }
            }
        }
    }

}

