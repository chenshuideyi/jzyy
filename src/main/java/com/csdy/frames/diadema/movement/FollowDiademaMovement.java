package com.csdy.frames.diadema.movement;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// 领域会始终跟随实体
public final class FollowDiademaMovement extends EntityDiademaMovement {
    public FollowDiademaMovement(Entity entity) {
        super(entity);
    }

    @Override public Vec3 getPosition() {
        return entity.position;
    }

    @Override public ServerLevel getLevel() {
        return (ServerLevel) entity.level;
    }
}
