package com.csdy.item.sword.saber;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SaberUtil {

    public static void Saber(Player player, float f){
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        List<LivingEntity> moblist = player.level.getEntitiesOfClass(LivingEntity.class,
                new AABB( x + 4, y + 4, z + 4, x -4, y -4, z -4));
        for (LivingEntity targets : moblist) {
            if (targets != null) {
                targets.invulnerableTime = 0;
                targets.hurt(new DamageSource(targets.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), player), (f));
            }
        }
    }

    public static void Point(Player player, float f){
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        List<LivingEntity> mobabcd = player.level.getEntitiesOfClass(LivingEntity.class, new AABB( x + 4, y + 4, z + 4, x -4, y -4, z -4));
        for (LivingEntity targets : mobabcd) {
            if (targets != null) {
                targets.invulnerableTime = 0;
                targets.hurt(new DamageSource(targets.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), player), (f));
            }
        }
    }
}
