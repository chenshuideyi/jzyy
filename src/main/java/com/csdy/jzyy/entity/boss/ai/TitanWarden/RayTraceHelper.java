package com.csdy.jzyy.entity.boss.ai.TitanWarden;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RayTraceHelper {

    public static class TraceResult {
        public List<BlockPos> blocks = new ArrayList<>();
        public List<LivingEntity> entities = new ArrayList<>();
    }

    /**
     * 在 from → to 的方向上做射线扫描
     *
     * @param from      起点实体
     * @param to        终点实体
     * @param length    射线长度（方块）；<=0 时自动取两实体距离
     * @param radius    圆柱半径（方块）
     * @param step      步进精度（方块）
     * @param filterEnt 额外过滤生物的条件（可 null）
     */
    public static TraceResult trace(Entity from,
                                    Entity to,
                                    double length,
                                    double radius,
                                    double step,
                                    Predicate<Entity> filterEnt) {

        Level level = from.level();
        TraceResult res = new TraceResult();
        Vec3 start =new Vec3(from.position().x,from.position().y+from.getBbHeight()*0.6f,from.position().z) ;
        Vec3 dir   = to.position().subtract(start).normalize();
        // 未指定长度 => 取实体间距离
        if (length <= 0) length = start.distanceTo(new Vec3(to.position().x,to.position().y+to.getBbHeight()*0.6f,to.position().z) );
        // 扫描
        for (double d = 0; d <= length; d += step) {
            Vec3 p = start.add(dir.scale(d));
            /* 1. 方块 */
            BlockPos bp = BlockPos.containing(p);
            if (!res.blocks.contains(bp)) {
                res.blocks.add(bp);
            }
            /* 2. 生物：以当前点为中心、半径为 radius 的 AABB */
            AABB box = new AABB(p.x - radius, p.y - radius, p.z - radius,
                    p.x + radius, p.y + radius, p.z + radius);

            List<LivingEntity> found = level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != null && e != from  && (filterEnt == null || filterEnt.test(e)));
            for (LivingEntity e : found) {
                if (!res.entities.contains(e)) {
                    res.entities.add(e);
                }
            }
        }
        return res;
    }

    /* 简易重载 */
    public static TraceResult trace(Entity from, Entity to, double length, double radius) {
        return trace(from, to, length, radius, 0.25, null);
    }
}
