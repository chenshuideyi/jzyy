package com.csdy.frames.diadema.range;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public abstract class DiademaRange {
    public abstract boolean ifInclude(Vec3 position);

    public abstract boolean ifInclude(Entity entity);

    public abstract Stream<Entity> getAffectingEntities();

    public abstract Stream<BlockPos> getAffectingBlocks();
}
