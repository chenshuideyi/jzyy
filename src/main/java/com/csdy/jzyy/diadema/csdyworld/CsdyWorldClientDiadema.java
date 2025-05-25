package com.csdy.jzyy.diadema.csdyworld;

import com.csdy.tcondiadema.frames.diadema.ClientDiadema;
import com.csdy.tcondiadema.particle.register.ParticlesRegister;
import com.csdy.tcondiadema.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

import static com.csdy.jzyy.diadema.csdyworld.CsdyWorldDiadema.RADIUS;

public class CsdyWorldClientDiadema extends ClientDiadema {
    private static final SimpleParticleType type = ParticleTypes.CRIT;
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        drawParticle(level);
    }

    private void drawParticle(Level level) {
        PointSets.Circle(RADIUS,160).map(vec3 -> vec3.add(getPosition())).forEach(vec3 -> level.addParticle(type, vec3.x,vec3.y,vec3.z,0,0,0));

    }

}
