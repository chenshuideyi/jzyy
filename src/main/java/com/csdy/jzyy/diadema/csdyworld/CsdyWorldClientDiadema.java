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

        // --- 2. 第一个竖直圆 (例如，在实体的局部XY平面) ---
        PointSets.Circle(RADIUS, 80)
                .map(sourceHorizontalPoint -> {
                    // sourceHorizontalPoint 是 PointSets.Circle 生成的原始XZ平面点 (x_src, 0, z_src)
                    double newRelativeX = sourceHorizontalPoint.x(); // 用原始的 x 作为新圆的 x
                    double newRelativeY = sourceHorizontalPoint.z(); // 用原始的 z 作为新圆的 y (高度)
                    double newRelativeZ = 0;                         // 新圆的 z 固定为0 (在XY平面上)

                    return entityPosition.add(newRelativeX, newRelativeY, newRelativeZ);
                })
                .forEach(particleWorldPos -> level.addParticle(type,
                        particleWorldPos.x(),
                        particleWorldPos.y() + yParticleDisplayOffset, // 应用统一的Y偏移
                        particleWorldPos.z(),
                        0, 0, 0));

        // --- 3. 第二个竖直圆 (例如，在实体的局部YZ平面, 与上一个竖直圆垂直) ---
        PointSets.Circle(RADIUS, 80)
                .map(sourceHorizontalPoint -> {
                    // sourceHorizontalPoint 是 PointSets.Circle 生成的原始XZ平面点 (x_src, 0, z_src)
                    double newRelativeX = 0;                         // 新圆的 x 固定为0 (在YZ平面上)
                    double newRelativeY = sourceHorizontalPoint.x(); // 用原始的 x 作为新圆的 y (高度)
                    // 注意：这里用 sourceHorizontalPoint.x() 作为高度，
                    // 而上一个竖直圆用 sourceHorizontalPoint.z() 作为高度。
                    // 这有助于确保即使 PointSets.Circle 生成的点在x和z上有特定模式，两个竖直圆也能良好地正交。
                    // 如果用 sourceHorizontalPoint.z() 作为高度，可能会与第一个竖直圆重叠或方向不理想。
                    double newRelativeZ = sourceHorizontalPoint.z(); // 用原始的 z 作为新圆的 z (深度)

                    return entityPosition.add(newRelativeX, newRelativeY, newRelativeZ);
                })
                .forEach(particleWorldPos -> level.addParticle(type,
                        particleWorldPos.x(),
                        particleWorldPos.y() + yParticleDisplayOffset, // 应用统一的Y偏移
                        particleWorldPos.z(),
                        0, 0, 0));
    }

}
