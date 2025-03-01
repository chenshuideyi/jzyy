package com.csdy.diadema.gula;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.DarkPartice;
import com.csdy.particle.register.ParticlesRegister;
import com.csdyms.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.csdyms.ParticleUtils.DrawCurve;
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class GulaClientDiadema extends ClientDiadema {

    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        Gula(level);
    }

    public void Gula(Level level){
        //影子
        Vec3 player = getPosition();
        double X = player.x;
        double Z = player.z;
        double Y = player.y+0.1;
        double r = sqrt(2)*8;

        Vec3 end = new Vec3(X+8, Y, Z+8);
        Vec3 start = new Vec3(X-8,Y,Z-8);
        Vec3 ctrlpoint = new Vec3(X-r,Y,Z+r);
        Vec3 ctrlpoint2 = new Vec3(X+r,Y,Z-r);


        SimpleParticleType type = ParticlesRegister.SHADOW_PARTICLE.get();
        SimpleParticleType type1 = ParticlesRegister.GULA_PARTICLE.get();

        DrawCurve(0.01,end,start,ctrlpoint,type,level);

        DrawCurve(0.01,end,start,ctrlpoint2,type,level);


        ///我想在具体实现上野蛮是可取的,所以不要问我这些数怎么来的还是硬编码,我目测猜的 by csdy

        //第三象限
        ParticleUtils.Drawline(0.05,X+r/2-1.5,Y,Z-r/-7-1.5,X+r/2+2.3,Y,Z-r/-7-3.0,type1,level);
        ParticleUtils.Drawline(0.05,X+r/2-1.5,Y,Z-r/-7-1.5,X+r/2+1.8,Y,Z-r/-7-4.5,type1,level);

//        level.addParticle(type1,X+r/2-1.5,Y,Z-r/-7-1.5,0,0,0);
//
//        level.addParticle(type1,X+r/2+2.3,Y,Z-r/-7-3.0,0,0,0);
//
//        level.addParticle(type1,X+r/2+1.8,Y,Z-r/-7-4.5,0,0,0);

        //第二象限
        ParticleUtils.Drawline(0.05,X-r/-7-1.5,Y,Z+r/2-1.5,X-r/-7-3.0,Y,Z+r/2+2.3,type1,level);
        ParticleUtils.Drawline(0.05,X-r/-7-1.5,Y,Z+r/2-1.5,X-r/-7-4.5,Y,Z+r/2+1.8,type1,level);

//        level.addParticle(type1,X-r/-7-1.5,Y,Z+r/2-1.5,0,0,0);
//
//        level.addParticle(type1,X-r/-7-3.0,Y,Z+r/2+2.3,0,0,0);
//
//        level.addParticle(type1,X-r/-7-4.5,Y,Z+r/2+1.8,0,0,0);

        //第四象限
        ParticleUtils.Drawline(0.05,X+r/-7+1.5,Y,Z-r/2+1.5,X+r/-7+3.0,Y,Z-r/2-2.3,type1,level);
        ParticleUtils.Drawline(0.05,X+r/-7+1.5,Y,Z-r/2+1.5,X+r/-7+4.5,Y,Z-r/2-1.8,type1,level);

//        level.addParticle(type1,X+r/-7+1.5,Y,Z-r/2+1.5,0,0,0);
//
//        level.addParticle(type1,X+r/-7+3.0,Y,Z-r/2-2.3,0,0,0);
//
//        level.addParticle(type1,X+r/-7+4.5,Y,Z-r/2-1.8,0,0,0);

        //第一象限
        ParticleUtils.Drawline(0.05,X-r/2+1.5,Y,Z+r/-7+1.5,X-r/2-2.3,Y,Z+r/-7+3.0,type1,level);
        ParticleUtils.Drawline(0.05,X-r/2+1.5,Y,Z+r/-7+1.5,X-r/2-1.8,Y,Z+r/-7+4.5,type1,level);

//        level.addParticle(type1,X-r/2+1.5,Y,Z+r/-7+1.5,0,0,0);
//
//        level.addParticle(type1,X-r/2-2.3,Y,Z+r/-7+3.0,0,0,0);
//
//        level.addParticle(type1,X-r/2-1.8,Y,Z+r/-7+4.5,0,0,0);

//        ParticleUtils.Drawline(0.01,X-r,Y,Z+r,X+r,Y,Z-r,type,level);
    }

}
