package com.csdy.frames.diadema.movement;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class DiademaMovement {
    public abstract Vec3 getPosition();
    public abstract Level getLevel();
}
