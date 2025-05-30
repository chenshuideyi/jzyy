package com.csdy.jzyy.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CsdyParticle extends TextureSheetParticle {

    protected CsdyParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.scale(0.5F);
        this.setSize(1.5F, 1.5F); // 粒子大小
        this.lifetime = 14; // 粒子存活时间（单位：刻）
        this.gravity = 0.0F; // 重力效果
        this.hasPhysics = false; // 是否受物理影响
    }

    @Override
    public ParticleRenderType getRenderType() {
        // 使用发光的渲染类型
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        // 设置粒子发光亮度（15 是最大亮度）
        return 15 << 20 | 15 << 4;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CsdyParticle particle = new CsdyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet); // 设置粒子纹理
            return particle;
        }
    }
}
