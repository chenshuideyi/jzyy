package com.csdy.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GulaParticle extends TextureSheetParticle {

    protected GulaParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.setSize(5F, 5F); // 粒子大小
        this.lifetime = 0; // 存活时间 1 刻（立即消失）
        this.xd = 0; // X 方向速度
        this.yd = 0; // Y 方向速度
        this.zd = 0; // Z 方向速度
        this.gravity = 0; // 无重力
        this.hasPhysics = false; // 无物理效果
    }

    @Override
    public void tick() {
        this.remove(); // 立即移除粒子
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }



    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GulaParticle particle = new GulaParticle(level, x, y, z);
            particle.pickSprite(this.spriteSet); // 设置粒子纹理
            return particle;
        }
    }
}
