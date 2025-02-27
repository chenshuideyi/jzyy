package com.csdy.diadema.abyss;

import com.csdy.diadema.ranges.HalfSphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Abyss extends Diadema {

    Player player = GetPlayer();
    static final double RADIUS = 6;

    public Abyss(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final HalfSphereDiademaRange range = new HalfSphereDiademaRange(this, RADIUS);

    @Override
    public DiademaRange getRange() {
        return range;
    }

    @Override
    protected void perTick() {
        Abyssutility(player);
    }

    public static void Abyssutility(Player player){
        Vec3 playerpos = player.position();
        double range = 6;
        Vec3 start = new Vec3(playerpos.x-range,playerpos.y-range,playerpos.z-range);
        Vec3 finish = new Vec3(playerpos.x+range,playerpos.y+range,playerpos.z+range);
        List<LivingEntity> moblist = player.level.getEntitiesOfClass(LivingEntity.class,
                new AABB(start,finish));
        for (LivingEntity targets : moblist) {
            if (targets != null && targets.position().distanceTo(playerpos)<=6 && !targets.equals(player))
                targets.setPos(targets.getX(),-500,targets.getZ());
            }
        }
    }


