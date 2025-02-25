package com.csdy.particleUtils;

import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public class PointSets {
    public static Stream<Vec3> Circle(double r, int seg) {
        return Arc(r, 2 * Math.PI, seg);
    }

    public static Stream<Vec3> Arc(double r, double rad, int seg) {
        return Stream.iterate(0, i -> i < seg, i -> i + 1).map(i -> {
            double theta = rad * i / seg;
            return new Vec3(r * Math.cos(theta), 0, r * Math.sin(theta));
        });
    }

    public static Stream<Vec3> Line(Vec3 from, Vec3 to, int seg) {
        return Stream.iterate(0, i -> i <= seg, i -> i + 1).map(i -> {
            double p = i / (double) seg;
            return from.scale(1 - p).add(to.scale(p));
        });
    }

    public static Stream<Vec3> FullRect(double sizeX, double sizeZ, int segX, int segZ) {
        return Stream.iterate(0, i -> i < segZ, i -> i + 1)
                .flatMap(i -> Stream.iterate(0, j -> j < segX, j -> j + 1)
                        .map(j -> new Vec3(sizeX * j / segX, 0, sizeZ * i / segZ)));
    }
}
