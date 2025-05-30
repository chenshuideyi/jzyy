package com.csdy.jzyy.diadema.csdyworld;

import com.csdy.jzyy.particle.register.JzyyParticlesRegister;
import com.csdy.tcondiadema.frames.diadema.ClientDiadema;
import com.csdy.tcondiadema.particle.register.ParticlesRegister;
import com.csdy.tcondiadema.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.csdy.jzyy.diadema.csdyworld.CsdyWorldDiadema.RADIUS;

public class CsdyWorldClientDiadema extends ClientDiadema {
    private static final SimpleParticleType type = JzyyParticlesRegister.CSDY_PARTICLE.get();
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        drawParticle(level);
    }

    private void drawParticle(Level level) {
        // 如果 type 和 RADIUS 不是字段，你可能需要将它们作为参数传入
        // 或者在此处定义它们
        // final double RADIUS = 1.5; // 举例
        // final ParticleOptions type = YourParticleType; // 举例

        Vec3 entityPosition = getPosition(); // 获取实体当前位置作为所有圆的基准点
        double yParticleDisplayOffset = 0.3; // 统一的Y轴显示偏移

        // --- 1. 水平圆 (在实体的XZ平面上) ---
        // PointSets.Circle 生成的点 vec3.y() 通常是0
        PointSets.Circle(RADIUS, 80)
                .map(entityPosition::add) // 直接将XZ平面的点加到实体位置
                .forEach(particleWorldPos -> level.addParticle(type,
                        particleWorldPos.x(),
                        particleWorldPos.y() + yParticleDisplayOffset, // 应用统一的Y偏移
                        particleWorldPos.z(),
                        0, 0, 0));

        // --- 1. 第一个竖直圆 (在实体的局部XY平面, X是宽度, Y是高度) ---
        PointSets.Circle(RADIUS, 80) // 生成XZ平面上的点
                .map(sourceHorizontalPoint -> {
                    double newRelativeX = sourceHorizontalPoint.x();
                    double newRelativeY = sourceHorizontalPoint.z(); // 用原始的z作为新圆的y (高度)
                    double newRelativeZ = 0;
                    // 返回相对坐标，方便后续filter
                    return new Vec3(newRelativeX, newRelativeY, newRelativeZ);
                })
                .filter(relativePos -> relativePos.y >= 0) // 只保留相对Y坐标 >= 0 的点 (上半圆)
                .forEach(relativePos -> {
                    Vec3 particleWorldPos = entityPosition.add(relativePos);
                    level.addParticle(type,
                            particleWorldPos.x(),
                            particleWorldPos.y() + yParticleDisplayOffset,
                            particleWorldPos.z(),
                            0, 0, 0);
                });

        // --- 2. 第二个竖直圆 (在实体的局部YZ平面, Y是高度, Z是深度) ---
        PointSets.Circle(RADIUS, 80) // 生成XZ平面上的点
                .map(sourceHorizontalPoint -> {
                    double newRelativeX = 0;
                    double newRelativeY = sourceHorizontalPoint.x(); // 用原始的x作为新圆的y (高度)
                    double newRelativeZ = sourceHorizontalPoint.z();
                    // 返回相对坐标，方便后续filter
                    return new Vec3(newRelativeX, newRelativeY, newRelativeZ);
                })
                .filter(relativePos -> relativePos.y >= 0) // 只保留相对Y坐标 >= 0 的点 (上半圆)
                .forEach(relativePos -> {
                    Vec3 particleWorldPos = entityPosition.add(relativePos);
                    level.addParticle(type,
                            particleWorldPos.x(),
                            particleWorldPos.y() + yParticleDisplayOffset,
                            particleWorldPos.z(),
                            0, 0, 0);
                });
    }

}
