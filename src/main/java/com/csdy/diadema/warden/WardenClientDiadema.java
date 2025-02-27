package com.csdy.diadema.warden;

import com.csdy.frames.diadema.ClientDiadema;
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

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        drawParticle(level);
    }


    private void drawParticle(Level level) {
        Vec3 center = getPosition();
        int segX = 25, segY = 75, segGround = 30;
        SimpleParticleType type = ParticlesRegister.DARK_PARTICLE.get();

        var points = PointSets.Circle(RADIUS, segX).flatMap(v -> { // 生成一个圆做基底，对其上的每个点：
            // 上半球
            var up = Stream.iterate(0, i -> i <= segY, i -> i + 1) // 这个方法相当于用for循环进行生成
                    .map(i -> v.scale(Math.sin(i / (double) segY * Math.PI / 2)) // 水平缩放
                            .add(0, RADIUS * Math.cos(i / (double) segY * Math.PI / 2), 0) // 垂直移动
                    );
            // 下半面
            var down = Stream.iterate(0, i -> i <= segGround, i -> i + 1)
                    .map(i -> v.scale((double) (i) / segGround) // 缩放
                            .yRot(i * 0.1f)  // 绕y轴轻微旋转
                    );
            return Stream.concat(up, down); // 把上下两部分合并起来
        }).map(v -> v.add(center)); // 移动中心点至center处

        points.forEach(v -> level.addParticle(type, v.x, v.y, v.z, 0, 0, 0)); //对每个位置绘制粒子
    }
}
