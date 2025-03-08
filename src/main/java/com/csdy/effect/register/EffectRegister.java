package com.csdy.effect.register;

import com.csdy.ModMain;
import com.csdy.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectRegister {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModMain.MODID);

    public static final RegistryObject<MobEffect> SCARED = EFFECTS.register("scared", Scared::new);
    public static final RegistryObject<MobEffect> PHYSICALINJURY = EFFECTS.register("physical_injury", PhysicalInjury::new);
    public static final RegistryObject<MobEffect> FRACTURE = EFFECTS.register("fracture", Fracture::new);
    public static final RegistryObject<MobEffect> COMMINUTED_FRACTURE = EFFECTS.register("comminuted_fracture", ComminutedFracture::new);
    public static final RegistryObject<MobEffect> WIND = EFFECTS.register("wind", Wind::new);
}
