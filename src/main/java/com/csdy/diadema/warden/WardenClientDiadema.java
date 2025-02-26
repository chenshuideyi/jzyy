package com.csdy.diadema.warden;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.particleUtils.PointSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public class WardenClientDiadema extends ClientDiadema {

    static final double RADIUS = WardenDiadema.RADIUS;


    // 粒子我建议像这样写在这里
    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        System.out.println("0000000");

        if (level == null) return;

        System.out.printf("%s, %s\n", level.dimension.location(), getDimension());

        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        System.out.println("1111111111");

        drawParticle(level);
    }


    private void drawParticle(Level level) {
        Vec3 center = getPosition();
        int segX = 40, segY = 80, segGround = 60;
        SimpleParticleType type = ParticlesRegister.DARK_PARTICLE.get();

        var points = PointSets.Circle(RADIUS, segX).flatMap(v -> {
            // 上半球
            var up = Stream.iterate(0, i -> i <= segY, i -> i + 1)
                    .map(i -> v.scale((double) (i) / segY).add(0, RADIUS * i / segY, 0));
            // 下半面
            var down = Stream.iterate(0, i -> i <= segGround, i -> i + 1)
                    .map(i -> v.scale((double) (i) / segGround));
            return Stream.concat(up, down);
        }).map(v -> v.add(center)); // 移动中心点

        points.forEach(v -> level.addParticle(type, v.x, v.y, v.z, 0, 0, 0)); //绘制
    }
}
