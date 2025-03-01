package com.csdy.particleUtils;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
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

    public static Stream<Vec3> Arc(double r, double from, double to, int seg) {
        return Arc(r, to - from, seg).map(v -> v.yRot((float) from));
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

    public static Stream<Vec3> BezierCurve(List<Vec3> controlPoints, int segments) {
        if (controlPoints.isEmpty()) {
            throw new IllegalArgumentException("controlPoints must not be empty");
        }
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be positive");
        }

        return IntStream.rangeClosed(0, segments)
                .mapToObj(i -> calculateBezierPoint(controlPoints, i / (double) segments));
    }

    private static Vec3 calculateBezierPoint(List<Vec3> controlPoints, double t) {
        List<Vec3> points = new ArrayList<>(controlPoints);
        int n = points.size();

        for (int k = 1; k < n; k++) {
            for (int i = 0; i < n - k; i++) {
                Vec3 p0 = points.get(i);
                Vec3 p1 = points.get(i + 1);

                double x = (1 - t) * p0.x + t * p1.x;
                double y = (1 - t) * p0.y + t * p1.y;
                double z = (1 - t) * p0.z + t * p1.z;

                points.set(i, new Vec3(x, y, z));
            }
        }

        return points.get(0);
    }
}
