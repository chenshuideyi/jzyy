package com.csdy.diadema.wind;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.register.ParticlesRegister;
import com.csdyms.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;


public class WindClientDiadema extends ClientDiadema {
    public void Wing(Level level) {
        //乘风行走之物
        Vec3 center = getPosition();
        double X = center.x;
        double Z = center.z;
        double Y = center.y+0.1;
        double value = 16;
        SimpleParticleType type =  ParticlesRegister.CUSTOM_PARTICLE.get();


        Random r = new Random();
        double randomX = r.nextDouble() * (value+value)-value;
        double randomY = r.nextDouble() * (value+value)-value;
        double randomZ = r.nextDouble() * (value+value)-value;

        double CX = r.nextDouble() * (value+value)-value;
        double CY = r.nextDouble() * (value+value)-value;
        double CZ = r.nextDouble() * (value+value)-value;

        Vec3 end = new Vec3(X+randomX, Y+randomY, Z+randomZ);
        Vec3 start = new Vec3(X-randomX,Y-randomY,Z-randomZ);
        Vec3 ctrlpoint = new Vec3(X+CX,Y+CY,Z+CZ);


        DrawCurve(0.01,end,start,ctrlpoint,type,level);

    }

    public void DrawCurve(double interval, Vec3 end, Vec3 start, Vec3 ctrlpoint,
                                 SimpleParticleType type, Level level) {
        // 计算两点之间的直线距离
        double deltax = end.x - start.x;
        double deltay = end.y - start.y;
        double deltaz = end.z - start.z;
        double length = Math.sqrt(deltax * deltax + deltay * deltay + deltaz * deltaz);
        int amount = (int) (length / interval);


        // 使用二次贝塞尔曲线公式计算曲线上的点
        for (int i = 0; i <= amount; i++) {
            double t = (double) i / amount;
            double x = (1 - t) * (1 - t) * start.x + 2 * (1 - t) * t * ctrlpoint.x + t * t * end.x;
            double y = (1 - t) * (1 - t) * start.y + 2 * (1 - t) * t * ctrlpoint.y + t * t * end.y;
            double z = (1 - t) * (1 - t) * start.z + 2 * (1 - t) * t * ctrlpoint.z + t * t * end.z;

            // 在曲线上添加粒子
            level.addParticle(type, x, y, z, 0, 0, 0);
        }
    }



//    public void DrawCurve(double interval, Vec3 end, Vec3 start, Vec3 ctrlpoint,
//                          SimpleParticleType type, Level level) {
//        // 计算两点之间的直线距离
//        double deltax = end.x - start.x;
//        double deltay = end.y - start.y;
//        double deltaz = end.z - start.z;
//        double length = Math.sqrt(deltax * deltax + deltay * deltay + deltaz * deltaz);
//        int amount = (int) (length / interval);
//        Random r = new Random();
//        double random = r.nextDouble() * (1+1)-1;
//
//        // 调整控制点的位置，使其远离直线路径
//        Vec3 direction = new Vec3(deltax, deltay, deltaz).normalize();
//        Vec3 perpendicular = new Vec3(-direction.y, direction.x, 0).normalize(); // 垂直于直线路径的方向
//        double curveStrength = 2.0; // 控制曲线的弯曲强度
//        Vec3 adjustedCtrlPoint = new Vec3(
//                (start.x + end.x) / 2 + perpendicular.x * curveStrength * length,
//                (start.y + end.y) / 2 + perpendicular.y * curveStrength * length,
//                (start.z + end.z) / 2 + perpendicular.z * curveStrength * length
//        );
//
//        // 使用二次贝塞尔曲线公式计算曲线上的点
//        for (int i = 0; i <= amount; i++) {
//            double t = (double) i / amount;
//            double x = (1 - t) * (1 - t) * start.x + 2 * (1 - t) * t * adjustedCtrlPoint.x + t * t * end.x;
//            double y = (1 - t) * (1 - t) * start.y + 2 * (1 - t) * t * adjustedCtrlPoint.y + t * t * end.y;
//            double z = (1 - t) * (1 - t) * start.z + 2 * (1 - t) * t * adjustedCtrlPoint.z + t * t * end.z;
//
//            // 在曲线上添加粒子
//            level.addParticle(type, x, y, z, random, random, random);
//        }
//    }

    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        Wing(level);
    }
}
