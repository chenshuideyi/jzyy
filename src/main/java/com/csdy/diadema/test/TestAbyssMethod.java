package com.csdy.diadema.test;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class TestAbyssMethod {
    public static void Abyss(Player player, float f){
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        double range = 4;
        List<LivingEntity> moblist = player.level.getEntitiesOfClass(LivingEntity.class, new AABB( x + range, y + range, z + range, x -range, y -range, z -range));
        for (LivingEntity targets : moblist) {
            if (targets != null) {
                targets.setPos(targets.getX(),-500,targets.getZ());

            }
        }
    }
}
