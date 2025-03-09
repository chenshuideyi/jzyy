package com.csdy.diadema.superbia;

import com.csdy.ParticleUtils;
import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static java.lang.Math.sqrt;
import static net.minecraft.util.Mth.square;

public class SuperbiaClinetDiadema extends ClientDiadema {
    private static final SimpleParticleType type = ParticlesRegister.SUPERNIAPARTICLE_PARTICLE.get();
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        drawParticle(level);
    }

    private void drawParticle(Level level) {
        PointSets.Circle(8,160).map(vec3 -> vec3.add(getPosition())).forEach(vec3 -> level.addParticle(type, vec3.x,vec3.y+0.1,vec3.z,0,0.05,0));
        Vec3 start = new Vec3(getPosition().x-3, getPosition().y+0.1,getPosition().z+1);
        Vec3 end = new Vec3(getPosition().x+3, getPosition().y+0.1,getPosition().z+1);
        Vec3 Lfrom = new Vec3(getPosition().x+3, getPosition().y+0.1,getPosition().z-1.5);
        Drawline(0.1,start,end,type,level);
        Drawline(0.1,Lfrom,end,type,level);
    }

    public void Drawline(double interval,Vec3 start, Vec3 end,SimpleParticleType type,Level level){
        //自动连接任意两点
        double deltax = start.x-end.x, deltay = start.y-end.y, deltaz = start.z-end.z;
        double length = sqrt(square(deltax) + square(deltay) + square(deltaz));
        int amount = (int)(length/interval);
        for (int i = 0 ; i <= amount; i++) {
            level.addParticle(type,end.x+deltax*i/amount, end.y+deltay*i/amount, end.z+deltaz*i/amount,0,0,0);
        }
    }
}
