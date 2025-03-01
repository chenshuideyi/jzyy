package com.csdy.diadema.meridiaVerse;

import com.csdy.frames.diadema.ClientDiadema;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.particleUtils.PointSets;
import com.csdy.particleUtils.Transforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public class MeridiaVerseClientDiadema extends ClientDiadema {
    private static final double RADIUS = com.csdy.diadema.meridiaVerse.MeridiaVerseDiadema.RADIUS;

    @Override protected void perTick() {
        var level = Minecraft.getInstance().level;

        if (level == null) return;
        if (!level.dimension.location().equals(getDimension())) return; //维度不一致时不触发

        drawParticle(level);
    }

    private float rx, ry, rz, rFin;
    private Vec3 finalAxis = new Vec3(1, 1, 1).normalize();
    private static final float SPD = 1f / 10;
    private static final float RS_X = SPD / 5, RS_Y = SPD / 13, RS_Z = SPD / 23, RS_FIN = SPD / 37, RS_AXIS = SPD / 47;
    private static final int SEG = 72;
    private static final SimpleParticleType PARTICLE_TYPE = ParticleTypes.ELECTRIC_SPARK;

    private void drawParticle(ClientLevel level) {
        Vec3 center = getPosition();
        SimpleParticleType type =  ParticlesRegister.MERIDIA_VERSE__PARTICLE.get();
        var round = PointSets.Circle(RADIUS, SEG).toList(); // 返回的stream只能用一次，但这个圆要重复用，所以收集成表

        rx += RS_X;
        ry += RS_Y;
        rz += RS_Z;
        rFin += RS_FIN;
        finalAxis = finalAxis.yRot(RS_AXIS); //恒定速度旋转所有轴

        var set = round.stream().map(v -> v.zRot(rz)); //第一个圆，转z
        set = Stream.concat(set, round.stream()).map(v -> v.xRot(rx)); // 连接第二个圆，一起转x
        set = Stream.concat(set, round.stream()).map(v -> v.yRot(ry)); // 连接第三个圆，一起转y
        set = set.map(v -> Transforms.Rotate(v, finalAxis, rFin)); // 最后整体沿最终轴转一下

        set.map(v -> v.add(center)) //移至中心
                .forEach(v -> level.addParticle(type, v.x, v.y, v.z, 0, 0, 0)); //绘制
    }
}
