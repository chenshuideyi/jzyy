package com.csdy.particle.register;

import com.csdy.ModMain;
import com.csdy.particle.*;
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

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticlesRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ModMain.MODID);

    public static final RegistryObject<SimpleParticleType> CUSTOM_PARTICLE = PARTICLE_TYPES.register("custom_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SHADOW_PARTICLE = PARTICLE_TYPES.register("shadow_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> EMC_PARTICLE = PARTICLE_TYPES.register("emc_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SULFUR_PARTICLE = PARTICLE_TYPES.register("sulfur_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DARK_PARTICLE = PARTICLE_TYPES.register("dark_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ABYSS_PARTICLE = PARTICLE_TYPES.register("abyss_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MERIDIA_VERSE__PARTICLE = PARTICLE_TYPES.register("meridia_verse_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GULA_PARTICLE = PARTICLE_TYPES.register("gula_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> KILL_AURA_PARTICLE = PARTICLE_TYPES.register("kill_aura_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MELTDOWN_PARTICLE = PARTICLE_TYPES.register("meltdown_particle", () -> new SimpleParticleType(false));


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientSetup(RegisterParticleProvidersEvent event) {
        // 注册粒子工厂
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.CUSTOM_PARTICLE.get(), CustomParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.SHADOW_PARTICLE.get(), ShadowPartice.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.EMC_PARTICLE.get(), EmcParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.SULFUR_PARTICLE.get(), SulfurParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.DARK_PARTICLE.get(), DarkPartice.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.MERIDIA_VERSE__PARTICLE.get(), MeridiaVerseParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.GULA_PARTICLE.get(), GulaParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.KILL_AURA_PARTICLE.get(), KillAuraParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticlesRegister.MELTDOWN_PARTICLE.get(), MeltDownPartice.Provider::new);
    }
}
