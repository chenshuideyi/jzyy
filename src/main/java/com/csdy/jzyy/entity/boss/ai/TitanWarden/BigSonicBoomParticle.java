package com.csdy.jzyy.entity.boss.ai.TitanWarden;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BigSonicBoomParticle extends HugeExplosionParticle {
    protected BigSonicBoomParticle(ClientLevel level, double x, double y, double z, double pQuadSizeMultiplier, SpriteSet pSprites) {
        super(level, x, y, z, pQuadSizeMultiplier,pSprites);
        this.setSize(100F, 100F); // 粒子大小
        this.lifetime = 16;
        this.quadSize = 1.5F;
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
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
            BigSonicBoomParticle particle = new BigSonicBoomParticle(level, x, y, z, xSpeed, this.spriteSet);
            particle.pickSprite(this.spriteSet); // 设置粒子纹理
            return particle;
        }
    }
}
