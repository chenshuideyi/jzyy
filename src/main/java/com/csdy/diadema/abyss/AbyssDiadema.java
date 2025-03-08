package com.csdy.diadema.abyss;

import com.csdy.diadema.api.ranges.ColumnDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class AbyssDiadema extends Diadema {
    static final double RADIUS = 6, HUP = 9, HDOWN = -9;

    public AbyssDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);

    }

    private final ColumnDiademaRange range = new ColumnDiademaRange(this, RADIUS, HDOWN, HUP);

    @Override public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!entity.equals(getPlayer()))
                entity.setPos(entity.getX(), -500, entity.getZ());
        }
    }
}


