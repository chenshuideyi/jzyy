package com.csdy.jzyy.particle.register;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.particle.CsdyParticle;
import com.csdy.jzyy.particle.SaberParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JzyyParticlesRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, JzyyModMain.MODID);

    public static final RegistryObject<SimpleParticleType> SABER_PARTICLE = PARTICLE_TYPES.register("saber_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CSDY_PARTICLE = PARTICLE_TYPES.register("csdy_particle", () -> new SimpleParticleType(false));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientSetup(RegisterParticleProvidersEvent event) {
        // 注册粒子工厂
        Minecraft.getInstance().particleEngine.register(JzyyParticlesRegister.SABER_PARTICLE.get(), SaberParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(JzyyParticlesRegister.CSDY_PARTICLE.get(), CsdyParticle.Provider::new);
    }
}
