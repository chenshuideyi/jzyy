package com.csdy.frames.diadema.movement;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// 领域会始终跟随实体
@SuppressWarnings("LombokGetterMayBeUsed")
public final class FollowDiademaMovement extends DiademaMovement {
    public FollowDiademaMovement(Entity entity) {
        this.entity = entity;
    }

    @Getter private final Entity entity;

    @Override public Vec3 getPosition() {
        return entity.position;
    }

    @Override public ServerLevel getLevel() {
        return (ServerLevel) entity.level;
    }
}
