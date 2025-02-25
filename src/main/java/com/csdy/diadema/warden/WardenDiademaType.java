package com.csdy.diadema.warden;

import com.csdy.diadema.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;

public class WardenDiademaType extends DiademaType {
    @Override public Diadema CreateInstance(DiademaMovement movement) {
        return new WardenDiadema(this, movement);
    }
}
