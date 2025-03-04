package com.csdy.diadema.fakemeltdown;

import com.csdy.diadema.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class FakeMeltdownDiadema extends Diadema {
    public FakeMeltdownDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    final static double RADIUS = 8;

    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    @Override public DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity living)) continue;
            living.setSecondsOnFire(50);

        }
    }
}
