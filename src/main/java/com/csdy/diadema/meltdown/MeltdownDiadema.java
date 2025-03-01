package com.csdy.diadema.meltdown;

import com.csdy.diadema.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;

public class MeltdownDiadema extends Diadema {
    public MeltdownDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    final static double RADIUS = 8;

    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    @Override public DiademaRange getRange() {
        return range;
    }
}
