package com.csdy.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MeltDownPartice extends TextureSheetParticle {

    protected MeltDownPartice(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSize(1F, 1F); // 粒子大小
        this.lifetime = 1; // 粒子存活时间（单位：刻）
        this.gravity = 0.0F; // 重力效果
        this.xd = 0; // X 方向速度
        this.yd = 0; // Y 方向速度
        this.zd = 0; // Z 方向速度
        this.hasPhysics = false; // 是否受物理影响

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE; // 渲染类型
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            MeltDownPartice particle = new MeltDownPartice(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet); // 设置粒子纹理
            return particle;
        }
    }
}
