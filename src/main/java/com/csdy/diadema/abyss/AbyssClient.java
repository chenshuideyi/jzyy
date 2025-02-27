package com.csdy.diadema.abyss;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.register.ParticlesRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AbyssClient extends ClientDiadema {
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        AddAbyssParticle(level);
    }


    private void AddAbyssParticle(Level level) {
        //无限垂直落下
        Vec3 center = getPosition();
        double y = 9;
        double X = center.x;
        double Z = center.z;

        SimpleParticleType type =  ParticlesRegister.ABYSS_PARTICLE.get();

        for(int i = 0 ; i <= 360;){
            double rad = i * 0.017453292519943295;
            double r1 = 5;
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            double Y = center.y+y;
            level.addParticle(type,X+x,Y,Z+z,0,0,0);
            i = i+10;
            if (y>-9) y = y-1;
            else y = 9;
        }

    }
}
