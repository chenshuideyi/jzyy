package com.csdy.diadema.luxuria;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.particleUtils.PointSets;
import com.csdy.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LuxuriaClinetDiadema extends ClientDiadema {
    private static final SimpleParticleType type = ParticlesRegister.LUXURIA_PARTICLE.get();
    private static final SimpleParticleType type1 = ParticleTypes.HEART.getType();
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        drawParticle(level);
    }

    private void drawParticle(Level level) {
        PointSets.Circle(1,60).map(vec3 -> vec3.add(getPosition())).forEach(vec3 -> level.addParticle(type, vec3.x-5.25,vec3.y+0.1,vec3.z,0,0,0));
        Vec3 start = getPosition().add(7.7,0.1,0);
        Vec3 end = getPosition().add(-7.7,0.1,0);
        PointSets.Circle(8,80).map(vec3 -> vec3.add(getPosition())).forEach(vec3 -> level.addParticle(type1, vec3.x,vec3.y+0.1,vec3.z,0,0.05,0));

        Vec3 ctrlpoint1 = getPosition().add(0,0.1,5.2);
        Vec3 ctrlpoint2 = getPosition().add(0,0.1,-5.2);
        ParticleUtils.DrawCurve(0.05,start,end,ctrlpoint1,type,level);
        ParticleUtils.DrawCurve(0.05,start,end,ctrlpoint2,type,level);
    }


}
