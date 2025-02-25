package com.csdy.diadema;

import com.csdy.ModMain;
import com.csdy.diadema.warden.WardenDiademaType;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.register.CsdyRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DiademaRegister {
    public static final DeferredRegister<DiademaType> DIADEMA_TYPES = DeferredRegister.create(CsdyRegistries.DIADEMA_TYPE, ModMain.MODID);

    public static final RegistryObject<DiademaType> WARDEN = DIADEMA_TYPES.register("warden", WardenDiademaType::new);
}
