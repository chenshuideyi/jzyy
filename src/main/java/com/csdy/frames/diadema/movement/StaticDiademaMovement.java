package com.csdy.frames.diadema.movement;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 领域会静止在特定地点
public final class StaticDiademaMovement extends DiademaMovement {
    private ServerLevel level;
    private Vec3 position;

    public StaticDiademaMovement(ServerLevel level, Vec3 position) {

        this.level = level;
        this.position = position;
    }

    @Override public Vec3 getPosition() {
        return position;
    }

    @Override public ServerLevel getLevel() {
        return level;
    }
}
