package com.csdy.frames.diadema.range;

import com.csdy.frames.diadema.Diadema;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.stream.Stream;

/// 方便使用的基础实现，只需要填入领域实例，然后写好粗略的AABB和具体判定方法即可 <br/>
/// 应保证AABB范围能完全包含住实际范围，不然AABB外面的部分会选不到
@SuppressWarnings("LombokGetterMayBeUsed")
public abstract class CommonDiademaRange extends DiademaRange {
    protected CommonDiademaRange(Diadema diadema) {
        this.diadema = diadema;
    }

    @Getter
    protected final Diadema diadema;

    /// 粗筛选用的AABB，应保证AABB范围能完全包含住实际范围，不然AABB外面的部分会选不到
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

    @Override public boolean ifInclude(Entity entity) {
        return entity.level == diadema.getLevel() &&
                (ifInclude(entity.position()) || ifInclude(entity.position.add(0, entity.eyeHeight, 0)));
    }
}
