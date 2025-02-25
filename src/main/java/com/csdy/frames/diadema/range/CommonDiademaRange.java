package com.csdy.frames.diadema.range;

import com.csdy.frames.diadema.Diadema;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.stream.Stream;

@SuppressWarnings("LombokGetterMayBeUsed")
public abstract class CommonDiademaRange extends DiademaRange {
    protected CommonDiademaRange(Diadema diadema) {
        this.diadema = diadema;
    }

    @Getter
    protected final Diadema diadema;

    protected abstract AABB getAABB();

    @Override public Stream<Entity> getAffectingEntities() {
        return diadema.getLevel()
                .getEntities((Entity) null, getAABB(), this::ifInclude)
                .stream();
    }

    @Override public Stream<BlockPos> getAffectingBlocks() {
        return BlockPos.betweenClosedStream(getAABB())
                .filter(pos -> ifInclude(pos.getCenter()));
    }
}
