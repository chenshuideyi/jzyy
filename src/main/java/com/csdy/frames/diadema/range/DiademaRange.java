package com.csdy.frames.diadema.range;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

/// 领域范围的判定，一般情况下建议继承CommonDiademaRange而非从头实现这个。
public abstract class DiademaRange {
    public abstract boolean ifInclude(Vec3 position);

    public abstract boolean ifInclude(Entity entity);

    public abstract Stream<Entity> getAffectingEntities();

    public abstract Stream<BlockPos> getAffectingBlocks();
}
