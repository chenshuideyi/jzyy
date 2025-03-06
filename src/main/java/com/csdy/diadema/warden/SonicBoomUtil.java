package com.csdy.diadema.warden;

import com.csdy.network.ParticleSyncing;
import com.csdy.network.packets.SonicBoomPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class SonicBoomUtil {

    public static void performSonicBoom(Level level, LivingEntity target, Player player) {
        if (target == null) return;
        if (player == null) return;

        target.hurt(new DamageSource(target.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), player), 30);

        Vec3 knockbackDirection = target.position().subtract(target.position()).normalize();
        target.knockback(1.0F, knockbackDirection.x, knockbackDirection.z);

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0F, 1.0F);

        ParticleSyncing.CHANNEL.send(PacketDistributor.NEAR.with(()->new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 128,
                        player.level.dimension()))
                ,new SonicBoomPacket(player.position,target.position));
//        ParticleUtils.Drawline(1,target.getX(), target.getY()+0.5, target.getZ(), player.getX(), player.getY()+0.5, player.getZ(),ParticleTypes.SONIC_BOOM,level );

    }
}

