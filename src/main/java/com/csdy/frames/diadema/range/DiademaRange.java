package com.csdy.frames.diadema.range;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public abstract class DiademaRange {
    public abstract boolean ifInclude(Vec3 position);

    public abstract boolean ifInclude(Entity entity);

    public abstract Set<Entity> getAffectingEntities();

    public abstract Set<BlockState> getAffectingBlocks();
}
