package com.csdy.particleUtils;

import net.minecraft.world.phys.Vec3;

public class Transforms {
    public static Vec3 Rotate(Vec3 vec, Vec3 axis, double rad) { //这段是ai写的
        // 1. 归一化旋转轴
        var unitAxis = axis.normalize();

        // 2. 计算旋转分量所需的三角函数
        double cosTheta = Math.cos(rad);
        double sinTheta = Math.sin(rad);

        // 3. 原始向量分解为平行和垂直于旋转轴的分量
        double dot = vec.dot(unitAxis);
        var parallel = unitAxis.scale(dot);         // 平行分量
        var perpendicular = vec.subtract(parallel); // 垂直分量

        // 4. 计算垂直分量的旋转方向（叉乘确定旋转平面）
        var w = unitAxis.cross(perpendicular);

        // 5. 合成旋转后的向量：平行分量 + 垂直分量旋转
        var rotatedPerpendicular = perpendicular.scale(cosTheta).add(w.scale(sinTheta));

        // 6. 最终结果 = 平行分量 + 旋转后的垂直分量
        return parallel.add(rotatedPerpendicular);
    }
}
