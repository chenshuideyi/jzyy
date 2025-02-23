package com.csdyms;

import com.csdy.particle.DarkPartice;
import com.csdy.particle.register.ParticlesRegister;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.*;
import static net.minecraft.util.Mth.square;

public class ParticleUtils {


    public static void Particle(Player player, Level level){

        int Particle1 = 7;
        double Particle = 1.0;

        for(double Angle = 0.0; Angle < 12.0; ++Angle) {
            SculkChargeParticleOptions type = new SculkChargeParticleOptions(4);
            double Rad = 360.0 * Angle / (double)Particle1 * Math.PI / 180.0;
            double X = player.getX() + Math.sin(Rad) * Particle;
            double Z = player.getZ() + Math.cos(Rad) * Particle;
            level.addParticle(type, X, player.getY() + 2, Z, ((new Random()).nextDouble() - 1.0) * 0.08, ((new Random()).nextDouble() - 1.0) * 0.08, ((new Random()).nextDouble() - 1.0) * 0.08);
        }

    }
    public static void Ball(Player player, Level level) {
        double CENTER_X = player.getX();
        double CENTER_Y = player.getY();
        double CENTER_Z = player.getZ();
        double RADIUS = 6;
        SimpleParticleType type =  ParticlesRegister.DARK_PARTICLE.get();
        int segX = 40;
        int segY = 80;
        for(int loopX = 0; loopX < segX; loopX++) {
            double angleX = loopX * 2 * PI / segX;
            for(int loopY = 0; loopY < segY; loopY++) {
                double angleY = loopY * PI / 2 / segY;
                double x = CENTER_X + RADIUS * Math.sin(angleY) * Math.cos(angleX);
                double y = CENTER_Y + RADIUS * Math.cos(angleY);
                double z = CENTER_Z + RADIUS * Math.sin(angleY) * Math.sin(angleX);

                Vec3 position = new Vec3(x, y, z);
                level.addParticle(type,position.x,position.y,position.z,0,0,0);
            }
        }
        // 遍历圆的区域
        for (double x = -RADIUS; x <= RADIUS; x += 0.15) {
            for (double z = -RADIUS; z <= RADIUS; z += 0.15) {
                // 检查当前点是否在圆内
                if (x * x + z * z <= RADIUS * RADIUS) {
                    // 在圆内，绘制粒子
                    level.addParticle(type, CENTER_X + x, CENTER_Y, CENTER_Z + z, 0, 0, 0);
                }
            }
        }
    }

    public static void Particle1(Player player, Level level){
        int X = (int) player.getX();
        int y = (int) player.getY();
        int Z = (int) player.getZ();

        SculkChargeParticleOptions type = new SculkChargeParticleOptions(3);
        for (int i = 0; i < 360; ++i) {
            double qwq = Math.toRadians(i);
            double QWQ = Math.cos(qwq);
            double qwq2 = Math.sin(qwq) + Z;
            level.addParticle(type,  QWQ * 1.5 + X , y + 0.1,  qwq2 * 1.5 +Z, 0, 1, 0);
        }
    }

    static int counter = 0;

    public static void Particle2(Player player,Level level){
                double X = player.getX();
                double Z = player.getZ();
                double y = player.getY() + 0.1D;
                SimpleParticleType type = ParticleTypes.ELECTRIC_SPARK.getType();
                for(int i = 0 ; i <= 360; i++){
                    double rad = i * 0.017453292519943295D;
                    double r1 = 0.5D,r2 = 3.0D;
            double x1 = r1 * Math.cos(rad) , x2 = r2 * Math.cos(rad);
            double z1 = r1 * Math.sin(rad) , z2 = r2 * Math.sin(rad);

            double tmp = 6D/Math.sqrt(2D);

            level.addParticle(type,X+x1,y,Z+z1,0,0,0);
            level.addParticle(type,X+x2,y,Z+z2,0,0,0);

            level.addParticle(type,X+x1,y,Z+z1+6,0,0,0);
            level.addParticle(type,X+x2,y,Z+z2+6,0,0,0);

            level.addParticle(type,X+x1,y,Z+z1-6,0,0,0);
            level.addParticle(type,X+x2,y,Z+z2-6,0,0,0);

            level.addParticle(type,X+x1+6,y,Z+z1,0,0,0);
            level.addParticle(type,X+x2+6,y,Z+z2,0,0,0);

            level.addParticle(type,X+x1-6,y,Z+z1,0,0,0);
            level.addParticle(type,X+x2-6,y,Z+z2,0,0,0);

            level.addParticle(type,X+x1+tmp,y,Z+z1+tmp,0,0,0);
            level.addParticle(type,X+x2+tmp,y,Z+z2+tmp,0,0,0);

            level.addParticle(type,X+x1-tmp,y,Z+z1-tmp,0,0,0);
            level.addParticle(type,X+x2-tmp,y,Z+z2-tmp,0,0,0);

            level.addParticle(type,X+x1+tmp,y,Z+z1-tmp,0,0,0);
            level.addParticle(type,X+x2+tmp,y,Z+z2-tmp,0,0,0);

            level.addParticle(type,X+x1-tmp,y,Z+z1+tmp,0,0,0);
            level.addParticle(type,X+x2-tmp,y,Z+z2+tmp,0,0,0);
        }

        for(double i = 0.5; i <= 3; i+=0.05D){
            for(int d = 0; d <=360 ; d+=60){
                double rad = (d+counter) * 0.017453292519943295;
                double rad1 = (d+counter-30) * 0.017453292519943295;
                double rad2 = (d+counter+30) * 0.017453292519943295;
                double x = i * Math.cos(rad),x1 = i * Math.cos(rad1);
                double z = i * Math.sin(rad),z1 = i * Math.cos(rad2);
                double tmp = 6/Math.sqrt(2);
                level.addParticle(type,X+x,y,Z+z,0,0,0);
                level.addParticle(type,X+x+6,y,Z+z,0,0,0);
                level.addParticle(type,X+x-6,y,Z+z,0,0,0);
                level.addParticle(type,X+x,y,Z+z+6,0,0,0);
                level.addParticle(type,X+x,y,Z+z-6,0,0,0);
                level.addParticle(type,X+x+tmp,y,Z+z+tmp,0,0,0);
                level.addParticle(type,X+x-tmp,y,Z+z-tmp,0,0,0);
                level.addParticle(type,X+x+tmp,y,Z+z-tmp,0,0,0);
                level.addParticle(type,X+x-tmp,y,Z+z+tmp,0,0,0);
            }
        }
        counter++;
        if(counter >= 360){
            counter = 0;
        }
    }

    public static void addParticle3(Player player,Level level){
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY();
        SimpleParticleType type = ParticleTypes.AMBIENT_ENTITY_EFFECT.getType();
        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;

            double r = 10.0D;
            double x = r * Math.cos(rad);
            double z = r * Math.sin(rad);
            level.addParticle(type,X+x,y,Z+z,0,0,0);
//            level.addParticle(type,X+x+3,y,Z+z+3,0,0,0);
//            level.addParticle(type,X+x-3,y,Z+z-3,0,0,0);
//            level.addParticle(type,X+x+3,y,Z+z-3,0,0,0);
//            level.addParticle(type,X+x-3,y,Z+z+3,0,0,0);
        }
//        for(double d = y ; d <= y+3; d+=0.05D){
//            level.addParticle(type,X-5,d,Z-5,0,1.0,0);
//            level.addParticle(type,X+5,d,Z+5,0,1.0,0);
//            level.addParticle(type,X-5,d,Z+5,0,1.0,0);
//            level.addParticle(type,X+5,d,Z-5,0,1.0,0);
//        }
    }
    public static void IDEA(Player player,Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double Y = player.getY() + 0.9D;
        double r = sqrt(4);
//        SimpleParticleType type = ParticleTypes.SOUL_FIRE_FLAME.getType();
        SimpleParticleType type = ParticleTypes.ENCHANT.getType();

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = sqrt(4);
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            level.addParticle(type,X+x,Y,Z+z,0,0,0);

        }

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = sqrt(4);
            double x = r1 * Math.cos(rad);
            double y = r1 * Math.sin(rad);
            level.addParticle(type,X+x,Y+y,Z,0,0,0);

        }

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = sqrt(4);
            double z = r1 * Math.cos(rad);
            double y = r1 * Math.sin(rad);
            level.addParticle(type,X,Y+y,Z+z,0,0,0);

        }


    }
    static double rs = 0;
    public static void Star(Player player,Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY() + 0.1D;
        double r = sqrt(25);
        int EdgeCount = 6;
        SimpleParticleType type = ParticleTypes.SOUL_FIRE_FLAME.getType();

        //六芒星
        //二维空间内距离原点长度为r且角度为a的点p坐标是：r*(cosa,sina)

        double[] x1 = new double[EdgeCount];
        double[] y1 = new double[EdgeCount];

        for(int i = 0 ; i < EdgeCount; i++){
            double rad = (2*Math.PI/EdgeCount)*i+rs;
            x1[i] = (r*cos(rad) + player.getX());
            y1[i] = (r*sin(rad) + player.getZ());
            //现场计算六芒星顶点
        }

        for(int i = 0 ; i < EdgeCount; i++){
            ParticleUtils.Drawline(0.05,x1[i], y, y1[i],x1[(i+2)%EdgeCount],y,y1[(i+2)%EdgeCount],type,level);
        }

        rs = rs+0.0015;


        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double x = r * Math.cos(rad);
            double z = r * Math.sin(rad);
            level.addParticle(type,X+x,y,Z+z,0,0,0);

        }
//             double r = sqrt(12);
//             double x = r * Math.cos(a);
//             double z = r * sin(a);
//            ParticleUtils.Drawline(0.05F,X+3,y,Z+sqrt(3),X-3,y,Z+sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z-sqrt(12),X-3,y,Z+sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z-sqrt(12),X+3,y,Z+sqrt(3),type,level);

//            ParticleUtils.Drawline(0.05F,X-3,y,Z-sqrt(3),X+3,y,Z-sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z+sqrt(12),X-3,y,Z-sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z+sqrt(12),X+3,y,Z-sqrt(3),type,level);


    }

    public static void miniStar(Player player,Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY() + 0.1D;
        double r = sqrt(2);
        SimpleParticleType type = ParticleTypes.ELECTRIC_SPARK.getType();
//        SimpleParticleType type = ParticleTypes.ENCHANT.getType();

        //六芒星
        //二维空间内距离原点长度为r且角度为a的点p坐标是：r*(cosa,sina)


        double[] x1 = new double[6];
        double[] y1 = new double[6];

        for(int i = 0 ; i < 6; i++){
            double rad = (2*Math.PI/6)*i+rs;
            x1[i] = (r*cos(rad) + player.getX());
            y1[i] = (r*sin(rad) + player.getZ());
            //现场计算六芒星顶点
        }

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = sqrt(2);
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            level.addParticle(type,X+x,y,Z+z,0,0,0);

        }

        for(int i = 0 ; i < 6; i++){
            ParticleUtils.Drawline(0.05,x1[i], y, y1[i],x1[(i+8)%6],y,y1[(i+8)%6],type,level);
        }

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = sqrt(2);
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            level.addParticle(type,X+x,y+1.85,Z+z,0,0,0);

        }

        for(int i = 0 ; i < 6; i++){
            ParticleUtils.Drawline(0.05,x1[i], y+1.85, y1[i],x1[(i+8)%6],y+1.85,y1[(i+8)%6],type,level);
            ParticleUtils.Drawline(0.05,x1[i], y, y1[i],x1[(i+8)%6],y+1.85,y1[(i+8)%6],type,level);
            ParticleUtils.Drawline(0.05,x1[i], y+1.85, y1[i],x1[(i+8)%6],y,y1[(i+8)%6],type,level);
        }


        rs = rs+0.00125;

//             double r = sqrt(12);
//             double x = r * Math.cos(a);
//             double z = r * sin(a);
//            ParticleUtils.Drawline(0.05F,X+3,y,Z+sqrt(3),X-3,y,Z+sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z-sqrt(12),X-3,y,Z+sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z-sqrt(12),X+3,y,Z+sqrt(3),type,level);

//            ParticleUtils.Drawline(0.05F,X-3,y,Z-sqrt(3),X+3,y,Z-sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z+sqrt(12),X-3,y,Z-sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z+sqrt(12),X+3,y,Z-sqrt(3),type,level);


    }

    public static void Test1(Player player,Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY() + 0.6D;
        double r = sqrt(12);
        SimpleParticleType type = ParticleTypes.FLAME.getType();
//        SimpleParticleType type = ParticleTypes.SOUL_FIRE_FLAME.getType();
//        SimpleParticleType type = ParticleTypes.ENCHANT.getType();

        //六芒星
        //二维空间内距离原点长度为r且角度为a的点p坐标是：r*(cosa,sina)


        double[] x1 = new double[6];
        double[] y1 = new double[6];

        for(int i = 0 ; i < 6; i++){
            double rad = (2*Math.PI/6)*i+rs;
            x1[i] = (r*cos(rad) + player.getX());
            y1[i] = (r*sin(rad) + player.getZ());
            //现场计算六芒星顶点
        }
        for(int i = 0 ; i < 6; i++){
            ParticleUtils.Drawline(0.05,x1[i], y-0.5, y1[i],x1[(i+8)%6],y+0.5,y1[(i+8)%6],type,level);
        }

        rs = rs+0.00125;

//             double r = sqrt(12);
//             double x = r * Math.cos(a);
//             double z = r * sin(a);
//            ParticleUtils.Drawline(0.05F,X+3,y,Z+sqrt(3),X-3,y,Z+sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z-sqrt(12),X-3,y,Z+sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z-sqrt(12),X+3,y,Z+sqrt(3),type,level);

//            ParticleUtils.Drawline(0.05F,X-3,y,Z-sqrt(3),X+3,y,Z-sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z+sqrt(12),X-3,y,Z-sqrt(3),type,level);
//            ParticleUtils.Drawline(0.05F,X,y,Z+sqrt(12),X+3,y,Z-sqrt(3),type,level);





    }
    public static void Rule(Player player,Level level) {
        //规矩
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY()+0.1;
        SimpleParticleType type = ParticleTypes.CRIT.getType();

        ParticleUtils.Drawline(0.05,X+5, y, Z-5,X+5,y,Z+5,type,level);
        ParticleUtils.Drawline(0.05,X+5, y, Z-5,X+5,y+10,Z-5,type,level);
        ParticleUtils.Drawline(0.05,X+5, y+10, Z-5,X+5,y+10,Z+5,type,level);

        ParticleUtils.Drawline(0.05,X+5, y, Z-5,X-5,y,Z-5,type,level);
        ParticleUtils.Drawline(0.05,X-5, y, Z-5,X-5,y+10,Z-5,type,level);
        ParticleUtils.Drawline(0.05,X+5, y+10, Z-5,X-5,y+10,Z-5,type,level);

        ParticleUtils.Drawline(0.05,X-5, y, Z-5,X-5,y,Z+5,type,level);
        ParticleUtils.Drawline(0.05,X-5, y, Z+5,X-5,y+10,Z+5,type,level);
        ParticleUtils.Drawline(0.05,X-5, y+10, Z-5,X-5,y+10,Z+5,type,level);


        ParticleUtils.Drawline(0.05,X-5, y, Z+5,X+5,y,Z+5,type,level);
        ParticleUtils.Drawline(0.05,X+5, y, Z+5,X+5,y+10,Z+5,type,level);
        ParticleUtils.Drawline(0.05,X-5, y+10, Z+5,X+5,y+10,Z+5,type,level);

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = 5;
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            level.addParticle(type,X+x,y,Z+z,0,0,0);

        }
    }
    public static void Oestrus(Player player,Level level){
        //和平
        double X = player.getX();
        double Z = player.getZ();
        double Y = player.getY()+0.1;
        SimpleParticleType type =  ParticlesRegister.SULFUR_PARTICLE.get();
//        SimpleParticleType type = ParticleTypes.GLOW.getType();
//        SimpleParticleType type = ParticleTypes.HEART.getType();

        for(int i = 0 ; i <= 360; i++){
            double rad = i * 0.017453292519943295;
            double r1 = 5;
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            level.addParticle(type,X+x,Y,Z+z,0,0,0);

        }
    }

    public static void Shadow(Player player,Level level){
        //影子
        double X = player.getX();
        double Z = player.getZ();
        double Y = player.getY()+0.1;
        double r = sqrt(128)/2.3;
        SimpleParticleType type = ParticlesRegister.SHADOW_PARTICLE.get();
//        SimpleParticleType type = ParticleTypes.END_ROD.getType();
        ParticleUtils.DrawCurve(0.01,X+4, Y, Z+4,X-4,Y,Z-4,X-r,Y,Z+r,type,level);

        ParticleUtils.DrawCurve(0.01,X+4, Y, Z+4,X-4,Y,Z-4,X+r,Y,Z-r,type,level);

        ParticleUtils.Drawline(0.01,X-r,Y,Z+r,X+r,Y,Z-r,type,level);
    }




    public static void Wing(Player player,Level level) {
        //乘风行走之物
        double X = player.getX();
        double Z = player.getZ();
        double Y = player.getY()+0.1;
        double value = 16;
        SimpleParticleType type =  ParticlesRegister.CUSTOM_PARTICLE.get();


        Random r = new Random();
        double randomX = r.nextDouble() * (value+value)-value;
        double randomY = r.nextDouble() * (value+value)-value;
        double randomZ = r.nextDouble() * (value+value)-value;

        double CX = r.nextDouble() * (value+value)-value;
        double CY = r.nextDouble() * (value+value)-value;
        double CZ = r.nextDouble() * (value+value)-value;

        ParticleUtils.DrawCurve(0.01,X+randomX, Y+randomY, Z+randomZ,X-randomX,Y-randomY,Z-randomZ,X+CX,Y+CY,Z+CZ,type,level);

    }
    public static void Death(Player player,Level level) {

        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY()+0.1;


        double hide = 3;
        double Yhide = 2;
        double slantinghide = 3;
//        SimpleParticleType type = ParticleTypes.SMOKE.getType();
        SimpleParticleType type = ParticleTypes.PORTAL.getType();
//        SimpleParticleType type = ParticleTypes.EXPLOSION_EMITTER.getType();
        //斜边
        ParticleUtils.DrawTiltedCircle(0.05,X+5, y+5, Z,X,y,Z,type,level);
        ParticleUtils.DrawTiltedCircle(0.05,X-5, y, Z,X,y+5,Z,type,level);
        ParticleUtils.DrawCurvedLinePositiveZ(0.05,X, y, Z,X,y+5,Z+5,type,level);
        ParticleUtils.DrawCurvedLinePositiveZ(0.05,X, y, Z,X,y+5,Z-5,type,level);
//        ParticleUtils.DrawCurve(0.05,X+5, y, Z,X,y,Z,type,level);

//        ParticleUtils.Drawline(0.05,X+slantinghide, y, Z-slantinghide,X-slantinghide,y,Z+slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X+slantinghide, y+Yhide, Z-slantinghide,X-slantinghide,y,Z+slantinghide,type,level);
//
//        ParticleUtils.Drawline(0.05,X+slantinghide, y, Z-slantinghide,X-slantinghide,y+Yhide,Z+slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X+slantinghide, y+Yhide, Z-slantinghide,X-slantinghide,y+Yhide,Z+slantinghide,type,level);
//
//        ParticleUtils.Drawline(0.05,X+slantinghide, y, Z+slantinghide,X-slantinghide,y,Z-slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X+slantinghide, y+Yhide, Z+slantinghide,X-slantinghide,y,Z-slantinghide,type,level);
//
//        ParticleUtils.Drawline(0.05,X+slantinghide, y, Z+slantinghide,X-slantinghide,y+Yhide,Z-slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X+slantinghide, y+Yhide, Z+slantinghide,X-slantinghide,y+Yhide,Z-slantinghide,type,level);

//        //锁链
//        ParticleUtils.Drawline(0.05,X+hide, y, Z,X-hide,y,Z,type,level);
//        ParticleUtils.Drawline(0.05,X+hide, y+Yhide, Z,X-hide,y,Z,type,level);
//        ParticleUtils.Drawline(0.05,X+hide, y, Z,X-hide,y+Yhide,Z,type,level);
//        ParticleUtils.Drawline(0.05,X+hide, y+Yhide, Z,X-hide,y+Yhide,Z,type,level);
//
//        //直边
//        ParticleUtils.Drawline(0.05,X, y, Z+hide,X,y,Z-hide,type,level);
//        ParticleUtils.Drawline(0.05,X, y+Yhide, Z+hide,X,y,Z-hide,type,level);
//        ParticleUtils.Drawline(0.05,X, y, Z+hide,X,y+Yhide,Z-hide,type,level);
//        ParticleUtils.Drawline(0.05,X, y+Yhide, Z+hide,X,y+Yhide,Z-hide,type,level);
//
//        //直边竖线
//        ParticleUtils.Drawline(0.05,X+hide, y+Yhide, Z,X+hide,y,Z,type,level);
//        ParticleUtils.Drawline(0.05,X-hide, y+Yhide, Z,X-hide,y,Z,type,level);
//        ParticleUtils.Drawline(0.05,X, y+Yhide, Z+hide,X,y,Z+hide,type,level);
//        ParticleUtils.Drawline(0.05,X, y+Yhide, Z-hide,X,y,Z-hide,type,level);
//
//        //斜边竖线
//        ParticleUtils.Drawline(0.05,X+slantinghide, y+Yhide, Z+slantinghide,X+slantinghide,y,Z+slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X+slantinghide, y+Yhide, Z-slantinghide,X+slantinghide,y,Z-slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X-slantinghide, y+Yhide, Z-slantinghide,X-slantinghide,y,Z-slantinghide,type,level);
//        ParticleUtils.Drawline(0.05,X-slantinghide, y+Yhide, Z+slantinghide,X-slantinghide,y,Z+slantinghide,type,level);

    }

    public static void Dust(Player player,Level level) {

        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY();



        double hide = 3;
        double Yhide = 2;
        double slantinghide = 3;
        SimpleParticleType type = ParticleTypes.SMOKE.getType();
        //斜边
        ParticleUtils.Drawline(0.05,X+slantinghide, y, Z-slantinghide,X-slantinghide,y,Z+slantinghide,type,level);
        ParticleUtils.Drawline(0.05,X+slantinghide, y, Z+slantinghide,X-slantinghide,y,Z-slantinghide,type,level);
        //直边
        ParticleUtils.Drawline(0.05,X, y, Z+hide,X,y,Z-hide,type,level);
        ParticleUtils.Drawline(0.05,X+hide, y, Z,X-hide,y,Z,type,level);

    }




    public static void Drawline(double interval,double tox, double toy, double toz, double X, double Y, double Z,SimpleParticleType type,Level level){
        //自动连接任意两点
        double deltax = tox-X, deltay = toy-Y, deltaz = toz-Z;
        double length = sqrt(square(deltax) + square(deltay) + square(deltaz));
        int amount = (int)(length/interval);
        for (int i = 0 ; i <= amount; i++) {
            level.addParticle(type,X+deltax*i/amount, Y+deltay*i/amount, Z+deltaz*i/amount,0,0,0);
        }
    }


    public static void DrawCurvedLinePositiveZ(double interval, double tox, double toy, double toz, double X, double Y, double Z, SimpleParticleType type, Level level) {
        // 计算两点之间的中点
        double midX = (X + tox) / 2;
        double midY = (Y + toy) / 2;
        double midZ = (Z + toz) / 2;

        // 控制点：在中点的基础上向正Z轴方向偏移
        double offset = 5.0; // 控制曲线的弯曲程度，值越大，曲线越弯曲
        double controlX = midX; // 控制点的X坐标（保持不变）
        double controlY = midY; // 控制点的Y坐标（保持不变）
        double controlZ = midZ + offset; // 控制点的Z坐标（向正Z轴方向偏移）

        // 计算两点之间的距离
        double deltax = tox - X, deltay = toy - Y, deltaz = toz - Z;
        double length = Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2) + Math.pow(deltaz, 2));
        int amount = (int)(length / interval);

        // 使用二次贝塞尔曲线公式生成曲线上的点
        for (int i = 0; i <= amount; i++) {
            double t = (double) i / amount;
            double curveX = Math.pow(1 - t, 2) * X + 2 * (1 - t) * t * controlX + Math.pow(t, 2) * tox;
            double curveY = Math.pow(1 - t, 2) * Y + 2 * (1 - t) * t * controlY + Math.pow(t, 2) * toy;
            double curveZ = Math.pow(1 - t, 2) * Z + 2 * (1 - t) * t * controlZ + Math.pow(t, 2) * toz;

            level.addParticle(type, curveX, curveY, curveZ, 0, 0, 0);
        }
    }
    public static void DrawCurve(double interval, double tox, double toy, double toz, double X, double Y, double Z, double ctrlX, double ctrlY, double ctrlZ, SimpleParticleType type, Level level) {
        // 计算两点之间的直线距离
        double deltax = tox - X;
        double deltay = toy - Y;
        double deltaz = toz - Z;
        double length = Math.sqrt(deltax * deltax + deltay * deltay + deltaz * deltaz);
        int amount = (int) (length / interval);

        // 使用二次贝塞尔曲线公式计算曲线上的点
        for (int i = 0; i <= amount; i++) {
            double t = (double) i / amount;
            double x = (1 - t) * (1 - t) * X + 2 * (1 - t) * t * ctrlX + t * t * tox;
            double y = (1 - t) * (1 - t) * Y + 2 * (1 - t) * t * ctrlY + t * t * toy;
            double z = (1 - t) * (1 - t) * Z + 2 * (1 - t) * t * ctrlZ + t * t * toz;

            // 在曲线上添加粒子
            level.addParticle(type, x, y, z, 0, 0, 0);
        }
    }
    public static void DrawTiltedCircle(double interval, double centerX, double centerY, double centerZ, double radius, double yAmplitude, double tiltAngle, SimpleParticleType type, Level level) {
        // 计算圆的分段数量
        int segments = (int) (2 * Math.PI / interval);

        // 遍历每个角度生成圆上的点
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments; // 当前角度

            // 计算圆的基本坐标（X和Z轴）
            double circleX = radius * Math.cos(angle);
            double circleZ = radius * Math.sin(angle);

            // 计算Y轴的变化（正弦波形式）
            double circleY = yAmplitude * Math.sin(angle);

            // 应用倾斜角度（绕X轴旋转）
            double tiltedX = circleX;
            double tiltedY = circleY * Math.cos(tiltAngle) - circleZ * Math.sin(tiltAngle);
            double tiltedZ = circleY * Math.sin(tiltAngle) + circleZ * Math.cos(tiltAngle);

            // 将圆平移到中心点
            double finalX = centerX + tiltedX;
            double finalY = centerY + tiltedY;
            double finalZ = centerZ + tiltedZ;

            // 在Level中添加粒子
            level.addParticle(type, finalX, finalY, finalZ, 0, 0, 0);
        }
    }
    public static void DrawO(double centerX, double centerY, double centerZ, double radius, double tiltX, double tiltY, double tiltZ, SimpleParticleType type, Level level) {
        // 角度间隔（1度）
        double angleIncrement = 0.017453292519943295; // 1度对应的弧度值

        // 遍历360度生成圆上的点
        for (int i = 0; i <= 360; i++) {
            double angle = i * angleIncrement; // 当前角度（弧度）

            // 圆的基本坐标（在XY平面上）
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            double z = 0; // 初始Z坐标为0（XY平面）

            // 应用旋转（绕X轴、Y轴、Z轴旋转）
            double rotatedX = x * Math.cos(tiltY) * Math.cos(tiltZ) +
                    y * (Math.cos(tiltZ) * Math.sin(tiltX) * Math.sin(tiltY) - Math.cos(tiltX) * Math.sin(tiltZ)) +
                    z * (Math.cos(tiltX) * Math.cos(tiltZ) * Math.sin(tiltY) + Math.sin(tiltX) * Math.sin(tiltZ));

            double rotatedY = x * Math.cos(tiltY) * Math.sin(tiltZ) +
                    y * (Math.cos(tiltX) * Math.cos(tiltZ) + Math.sin(tiltX) * Math.sin(tiltY) * Math.sin(tiltZ)) +
                    z * (-Math.cos(tiltZ) * Math.sin(tiltX) + Math.cos(tiltX) * Math.sin(tiltY) * Math.sin(tiltZ));

            double rotatedZ = -x * Math.sin(tiltY) +
                    y * Math.cos(tiltY) * Math.sin(tiltX) +
                    z * Math.cos(tiltX) * Math.cos(tiltY);

            // 将圆平移到中心点
            double finalX = centerX + rotatedX;
            double finalY = centerY + rotatedY;
            double finalZ = centerZ + rotatedZ;

            // 在Level中添加粒子
            level.addParticle(type, finalX, finalY, finalZ, 0, 0, 0);
        }
    }

}
