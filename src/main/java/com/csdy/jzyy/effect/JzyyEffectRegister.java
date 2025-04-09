package com.csdy.jzyy.effect;


import com.csdy.jzyy.ModMain;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JzyyEffectRegister {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModMain.MODID);

    public static final RegistryObject<MobEffect> OVERCHARGE = EFFECTS.register("overcharge", Overcharge::new);
    public static final RegistryObject<MobEffect> OVERCHARGE_ARMOR = EFFECTS.register("overcharge_armor", OverchargeArmor::new);
}
