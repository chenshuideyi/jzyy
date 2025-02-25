package com.csdy.diadema.warden;

import com.csdy.diadema.ranges.HalfSphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;

public class WardenDiadema extends Diadema {
    public WardenDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final HalfSphereDiademaRange range = new HalfSphereDiademaRange(this, 32);

    @Override protected DiademaRange getRange() {
        return range;
    }
}
