package com.csdy.frames.diadema.movement;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public abstract class DiademaMovement {
    public abstract Vec3 getPosition();
    public abstract ServerLevel getLevel();
}
