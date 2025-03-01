package com.csdy.diadema.meltdown;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.stream.Stream;

public class MeltdownClientDiadema extends ClientDiadema {

    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        addParticle(level);
    }


    private static final SimpleParticleType PARTICLE_TYPE = ParticleTypes.ELECTRIC_SPARK;
    private static final double RADIUS = MeltdownDiadema.RADIUS;
    private static final double R1 = 0.2, R2 = 0.3, R_OUT = 1.1; // 外环2.5，内两环分别是0.5和0.75，比例是0.2和0.3
    private static final int SEG1 = 24, SEG2 = 8, SEG3 = 24, SEG_LINE = 16, SEG_OUT = 120;

    private void addParticle(Level level) {
        var pos = getPosition();
        var arcs = Stream.iterate(1, i -> i + 4).limit(3).flatMap(i -> {
            var r1 = i / 6.0 * Math.PI;
            var r2 = (i + 2) / 6.0 * Math.PI;
            return Stream.concat(PointSets.Arc(RADIUS, r1, r2, SEG3), PointSets.Arc(RADIUS * R2, r1, r2, SEG2));
        });
        var lines = Stream.iterate(1, i -> i + 2).limit(6).flatMap(i -> {
            var r = i / 6.0 * Math.PI;
            var p1 = new Vec3(RADIUS, 0, 0).yRot((float) r);
            return PointSets.Line(p1, p1.scale(R2), SEG_LINE);
        });
        var points = Stream.concat(Stream.concat(arcs, lines), PointSets.Circle(RADIUS * R1, SEG1));
        points = Stream.concat(points, PointSets.Circle(RADIUS * R_OUT, SEG_OUT)); // 外环

        points.map(v -> v.add(pos)).forEach(v -> level.addParticle(PARTICLE_TYPE, v.x, v.y, v.z, 0, 0.05, 0));
    }
}
