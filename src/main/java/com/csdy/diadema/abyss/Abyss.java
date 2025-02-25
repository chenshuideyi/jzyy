package com.csdy.diadema.abyss;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Abyss {

    public static void Abyssutility(Player player){
        Vec3 playerpos = player.position();
        double range = 4;
        Vec3 start = new Vec3(playerpos.x-range,playerpos.y-range,playerpos.z-range);
        Vec3 finish = new Vec3(playerpos.x+range,playerpos.y+range,playerpos.z+range);
        List<LivingEntity> moblist = player.level.getEntitiesOfClass(LivingEntity.class,
                new AABB(start,finish));
        for (LivingEntity targets : moblist) {
            if (targets != null && targets.position().distanceTo(playerpos)<=4) {
                targets.setPos(targets.getX(),-500,targets.getZ());
            }
        }
    }

}
