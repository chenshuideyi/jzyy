package com.csdy.jzyy.entity.boss.ai.TitanWarden;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static TraceResult trace(Entity from, Entity to, double length,
                                    double radius, double step, Predicate<Entity> filterEnt) {

        if (from == null || to == null) {
            return new TraceResult();
        }

        Level level = from.level();

        TraceResult res = new TraceResult();

        Set<BlockPos> blockSet = new HashSet<>();
        Set<LivingEntity> entitySet = new HashSet<>();

        Vec3 start = new Vec3(from.position().x, from.position().y + from.getBbHeight() * 0.575f, from.position().z);
        Vec3 endVec = new Vec3(to.position().x, to.position().y + to.getBbHeight() * 0.575f, to.position().z);
        Vec3 diff = endVec.subtract(start);

        if (diff.lengthSqr() < 1e-8) {
            return res;
        }

        Vec3 dir = diff.normalize();
        if (length <= 0) length = diff.length();

        AABB overallBox = new AABB(start, start.add(dir.scale(length)))
                .inflate(radius);
        List<LivingEntity> potentialEntities = level.getEntitiesOfClass(LivingEntity.class, overallBox,
                e -> e != null && e != from && (filterEnt == null || filterEnt.test(e)));

        for (double d = 0; d <= length; d += step) {
            Vec3 p = start.add(dir.scale(d));
            BlockPos bp = BlockPos.containing(p);
            blockSet.add(bp);
            for (LivingEntity entity : potentialEntities) {
                if (entity.getBoundingBox().inflate(radius).contains(p)) {
                    entitySet.add(entity);
                }
            }
        }

        res.blocks.addAll(blockSet);
        res.entities.addAll(entitySet);

        return res;
    }

    public static TraceResult trace(Entity from, Entity to, double length, double radius) {
        return trace(from, to, length, radius, 0.25, null);
    }

    //太几把卡了

    public static TraceResult tracea(Entity from,
                                    Entity to,
                                    double length,
                                    double radius,  // 检测实体的半径
                                    double blockRadius,   // 检测方块的半径
                                    double step,
                                    Predicate<Entity> filterEnt) {

        Level level = from.level();
        TraceResult res = new TraceResult();
        Vec3 start =new Vec3(from.position().x,from.position().y+from.getBbHeight()*0.575f*5f,from.position().z) ;
        Vec3 dir   = to.position().subtract(start).normalize();
        // 未指定长度 => 取实体间距离
        if (length <= 0) length = start.distanceTo(new Vec3(to.position().x,to.position().y+to.getBbHeight()*0.575f*5f,to.position().z) );
        // 扫描
        for (double d = 0; d <= length; d += step) {
            Vec3 p = start.add(dir.scale(d));
            //1. 方块检测（扩大范围）
            BlockPos center = BlockPos.containing(p);
            // 检测 blockRadius 范围内的所有方块
            for (int x = (int) -blockRadius; x <= blockRadius; x++) {
                for (int y = (int) -blockRadius; y <= blockRadius; y++) {
                    for (int z = (int) -blockRadius; z <= blockRadius; z++) {
                        BlockPos bp = center.offset(x, y, z);
                        if (!res.blocks.contains(bp)) {
                            res.blocks.add(bp);
                        }
                    }
                }
            }
            //2. 生物：以当前点为中心、半径为 radius 的 AABB
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

    public static TraceResult tracea(Entity from, Entity to, double length, double radius, double blockRadius) {
        return tracea(from, to, length, radius, blockRadius,0.25, null);
    }

}
