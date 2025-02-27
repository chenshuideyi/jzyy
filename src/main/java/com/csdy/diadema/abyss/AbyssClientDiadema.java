package com.csdy.diadema.abyss;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class AbyssClientDiadema extends ClientDiadema {
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        addParticle(level);
    }


    private static final SimpleParticleType PARTICLE_TYPE = ParticlesRegister.ABYSS_PARTICLE.get();
    private static final double RADIUS = AbyssDiadema.RADIUS, LENGTH = 8, HUP = AbyssDiadema.HUP, HDOWN = AbyssDiadema.HDOWN;
    private static final float SPEED = (float) (6 * Math.PI / 180); // 每tick转6°
    private static final int LINES = 3; // 螺旋线数量
    private static final int SEGY = 16;

    private float rad;

    private void addParticle(Level level) {
        rad += SPEED;

        var points = PointSets.Circle(RADIUS, LINES) // 先圆周上找LINES的点（螺旋线的数量
                .map(v -> v.yRot(rad).add(getPosition())) // 旋转一个rad，然后用加法把圆心移动到自己的中心处
                // 对每个点生成一条上方HUP处到上方HUP-LENGTH的长度为LENGTH的线，然后Flat在一起
                .flatMap(v -> PointSets.Line(v.add(0, HUP, 0), v.add(0, HUP - LENGTH, 0), SEGY));

        // 对每个点绘制粒子，让粒子向下移动HDOWN - HUP - LENGTH，即把线的下端移动到领域最下方刚好停止
        points.forEach(v -> level.addParticle(PARTICLE_TYPE, v.x, v.y, v.z, 0, HDOWN - HUP - LENGTH, 0));
    }


//    private void AddAbyssParticle(Level level) {
//        //无限垂直落下
//        Vec3 center = getPosition();
//        double y = 9;
//        double X = center.x;
//        double Z = center.z;
//
//        for (int i = 0; i <= 360; ) {
//            double rad = i * 0.017453292519943295;
//            double r1 = 5;
//            double x = r1 * Math.cos(rad);
//            double z = r1 * Math.sin(rad);
//            double Y = center.y + y;
//            level.addParticle(PARTICLE_TYPE, X + x, Y, Z + z, 0, 0, 0);
//            i = i + 10;
//            if (y > -9) y = y - 1;
//            else y = 9;
//        }
//    }
}
