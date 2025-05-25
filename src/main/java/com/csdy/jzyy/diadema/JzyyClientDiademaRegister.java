package com.csdy.jzyy.diadema;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.diadema.csdyworld.CsdyWorldClientDiadema;
import com.csdy.jzyy.diadema.meltdown.AlexMeltdownClientDiadema;
import com.csdy.tcondiadema.frames.CsdyRegistries;
import com.csdy.tcondiadema.frames.diadema.ClientDiademaType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


@OnlyIn(Dist.CLIENT)
public class JzyyClientDiademaRegister {
    public static final DeferredRegister<ClientDiademaType> CLIENT_DIADEMA_TYPES = DeferredRegister.create(CsdyRegistries.CLIENT_DIADEMA_TYPE, ModMain.MODID);

    public static final RegistryObject<ClientDiademaType> ALEX_MELTDOWN =
            CLIENT_DIADEMA_TYPES.register("alex_meltdown", () -> ClientDiademaType.Create(AlexMeltdownClientDiadema::new));

    public static final RegistryObject<ClientDiademaType> CSDY_WORLD =
            CLIENT_DIADEMA_TYPES.register("csdy_world", () -> ClientDiademaType.Create(CsdyWorldClientDiadema::new));
}
