package com.csdy.frames.diadema.movement;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class StaticDiademaMovement extends DiademaMovement {
    private final Level level;
    private final Vec3 position;

    public StaticDiademaMovement(Level level, Vec3 position) {

        this.level = level;
        this.position = position;
    }

    @Override public Vec3 getPosition() {
        return position;
    }

    @Override public Level getLevel() {
        return level;
    }
}
