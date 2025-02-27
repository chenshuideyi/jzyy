package com.csdy.diadema.abyss;

import com.csdy.diadema.ranges.ColumnDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AbyssDiadema extends Diadema {
    static final double RADIUS = 6, HUP = 9, HDOWN = -9;

    public AbyssDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);

    }

    private final ColumnDiademaRange range = new ColumnDiademaRange(this, RADIUS, HDOWN, HUP);

    @Override public DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!entity.equals(getPlayer()))
                entity.setPos(entity.getX(), -500, entity.getZ());
        }
    }

//    public static void Abyssutility(Player player) {
//        Vec3 playerpos = player.position();
//        double range = 5;
//        Vec3 start = new Vec3(playerpos.x - range, playerpos.y - range, playerpos.z - range);
//        Vec3 finish = new Vec3(playerpos.x + range, playerpos.y + range, playerpos.z + range);
//        List<LivingEntity> moblist = player.level.getEntitiesOfClass(LivingEntity.class,
//                new AABB(start, finish));
//        for (LivingEntity targets : moblist) {
//            if (targets != null && targets.position().distanceTo(playerpos) <= 6 && !targets.equals(player))
//                targets.setPos(targets.getX(), -500, targets.getZ());
//        }
//    }
}


