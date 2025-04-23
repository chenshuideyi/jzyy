package com.csdy.jzyy.diadema;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.diadema.meltdown.AlexMeltdownDiadema;
import com.csdy.tcondiadema.frames.CsdyRegistries;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JzyyDiademaRegister {
    public static final DeferredRegister<DiademaType> DIADEMA_TYPES = DeferredRegister.create(CsdyRegistries.DIADEMA_TYPE, ModMain.MODID);

    public static final RegistryObject<DiademaType> ALEX_MELTDOWN =
            DIADEMA_TYPES.register("alex_meltdown", () -> DiademaType.create(AlexMeltdownDiadema::new));

}
