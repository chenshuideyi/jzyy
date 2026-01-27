package com.csdy.jzyy.effect.register;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JzyyEffectRegister {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JzyyModMain.MODID);

    public static final RegistryObject<MobEffect> OVERCHARGE = EFFECTS.register("overcharge", Overcharge::new);
    public static final RegistryObject<MobEffect> OVERCHARGE_ARMOR = EFFECTS.register("overcharge_armor", OverchargeArmor::new);
    public static final RegistryObject<MobEffect> TRUE_MAN_LAST_DANCE = EFFECTS.register("true_man_last_dance", TrueManLastDance::new);
    public static final RegistryObject<MobEffect> DIVINITY = EFFECTS.register("divinity", Divinity::new);
    public static final RegistryObject<MobEffect> HOLY_PROTECTION_COOLDOWN = EFFECTS.register("holy_protection_cooldown", HolyProtectionCooldown::new);
    public static final RegistryObject<MobEffect> CSDY = EFFECTS.register("csdy", Csdy::new);
    public static final RegistryObject<MobEffect> HOROLOGIUM_NO_AI = EFFECTS.register("horologium_no_ai", HorologiumNoAI::new);
    public static final RegistryObject<MobEffect> DEEP_WOUND = EFFECTS.register("deep_wound", DeepWound::new);
    public static final RegistryObject<MobEffect> DRAGON_GAS = EFFECTS.register("dragon_gas", DragonGas::new);


}
