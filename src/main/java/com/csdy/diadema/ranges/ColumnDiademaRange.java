package com.csdy.diadema.ranges;

import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.range.CommonDiademaRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class ColumnDiademaRange extends CommonDiademaRange {

    public ColumnDiademaRange(Diadema diadema, double radius, double btmHeight, double topHeight) {
        super(diadema);
        this.radius = radius;
        this.topHeight = topHeight;
        this.btmHeight = btmHeight;
    }

    @Getter @Setter
    private double radius;
    @Getter @Setter
    private double topHeight;

    @Getter @Setter
    private double btmHeight;

    @Override protected AABB getAABB() {
        var pos = diadema.getPosition();
        var a = new Vec3(radius, topHeight, radius);
        var b = new Vec3(-radius, btmHeight, -radius);
        return new AABB(pos.add(b), pos.add(a));
    }

    @Override public boolean ifInclude(Vec3 position) {
        var pos = position.subtract(diadema.getPosition());
        return pos.y >= btmHeight
                && pos.y <= topHeight
                && pos.x * pos.x + pos.z * pos.z <= radius * radius;
    }
}
