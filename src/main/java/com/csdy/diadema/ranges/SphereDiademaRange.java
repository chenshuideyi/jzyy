package com.csdy.diadema.ranges;

import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.range.CommonDiademaRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class SphereDiademaRange extends CommonDiademaRange {
    public SphereDiademaRange(Diadema diadema, double radius) {
        super(diadema);
        this.radius = radius;
    }

    @Getter @Setter
    private double radius;

    @Override protected AABB getAABB() {
        var pos = diadema.getPosition();
        var r = new Vec3(radius, radius, radius);
        return new AABB(pos.add(r.reverse()), pos.add(r)); // pos - r, pos + r
    }

    @Override public boolean ifInclude(Vec3 position) {
        return position.distanceToSqr(diadema.getPosition()) <= radius * radius;
    }

    @Override public boolean ifInclude(Entity entity) {
        return entity.level == diadema.getLevel() && ifInclude(entity.position());
    }
}
