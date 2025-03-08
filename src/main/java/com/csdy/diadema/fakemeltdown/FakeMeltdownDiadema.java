package com.csdy.diadema.fakemeltdown;

import com.csdy.diadema.api.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class FakeMeltdownDiadema extends Diadema {
    public FakeMeltdownDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    final static double RADIUS = 8;

    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    @Override public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity living)) continue;
            living.setSecondsOnFire(50);

        }
    }
}
