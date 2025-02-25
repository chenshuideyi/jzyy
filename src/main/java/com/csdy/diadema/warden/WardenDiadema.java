package com.csdy.diadema.warden;

import com.csdy.diadema.ranges.HalfSphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.particleUtils.PointSets;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.stream.Stream;

// 如你所见，这个是具体实例，后面带Type的是类型，一般而言两个都要重写一份
public class WardenDiadema extends Diadema {
    private static final double RADIUS = 6;

    public WardenDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final HalfSphereDiademaRange range = new HalfSphereDiademaRange(this, RADIUS);

    @Override public DiademaRange getRange() {
        return range;
    }

    // 粒子我建议像这样写在这里，还有大部分逐帧事件（比如把所有范围内实体丢进虚空）就都可以写在这里，有方法能获取所有影响到的方块和实体，针对类型的最好写在Type类上
    // 需要针对实体进出的时间点的就监听事件，参考WardenDiadema上的那俩事件处理器。
    // todo: ↑得写在客户端的部分所以暂时当我没说
    @Override protected void perTick() {
    }

    private void drawParticle() {
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

        points.forEach(v -> getLevel().addParticle(type, v.x, v.y, v.z, 0, 0, 0)); //绘制
    }
}
