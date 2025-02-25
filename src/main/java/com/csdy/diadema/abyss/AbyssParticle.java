package com.csdy.diadema.abyss;

import com.csdy.particle.register.ParticlesRegister;
import com.csdyms.ParticleUtils;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

public class AbyssParticle {

    public static void AddAbyssParticle(Player player, Level level) {
        //无限垂直落下
        double y = 9;
        double X = player.getX();
        double Z = player.getZ();

        SimpleParticleType type =  ParticlesRegister.ABYSS_PARTICLE.get();

        for(int i = 0 ; i <= 360;){
            double rad = i * 0.017453292519943295;
            double r1 = 5;
            double x = r1 * Math.cos(rad);
            double z = r1 * Math.sin(rad);
            double Y = player.getY()+y;
            level.addParticle(type,X+x,Y,Z+z,0,0,0);
            i = i+10;
            if (y>-9) y = y-1;
            else y = 9;
        }

    }


}
