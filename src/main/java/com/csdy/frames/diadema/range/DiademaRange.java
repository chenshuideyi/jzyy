package com.csdy.frames.diadema.range;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public abstract class DiademaRange {
    public abstract boolean isInRange(Vec3 position);

    public abstract boolean isInRange(Entity entity);

    public abstract Set<Entity> getAffectingEntities(Level level, Vec3 position);

    public abstract Set<BlockState> getAffectingBlocks(Level level, Vec3 position);
}
