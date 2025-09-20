package com.csdy.jzyy.diadema;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.diadema.csdyworld.CsdyWorldClientDiadema;
import com.csdy.jzyy.diadema.meltdown.LuckyTntMeltdownClientDiadema;
import com.csdy.jzyy.diadema.miziao.MusicGameClientDiadema;
import com.csdy.tcondiadema.frames.CsdyRegistries;
import com.csdy.tcondiadema.frames.diadema.ClientDiademaType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


@OnlyIn(Dist.CLIENT)
public class JzyyClientDiademaRegister {
    public static final DeferredRegister<ClientDiademaType> CLIENT_DIADEMA_TYPES = DeferredRegister.create(CsdyRegistries.CLIENT_DIADEMA_TYPE, JzyyModMain.MODID);

    public static final RegistryObject<ClientDiademaType> LUCKY_TNT_MELTDOWN =
            CLIENT_DIADEMA_TYPES.register("lucky_tnt_meltdown", () -> ClientDiademaType.Create(LuckyTntMeltdownClientDiadema::new));

    public static final RegistryObject<ClientDiademaType> CSDY_WORLD =
            CLIENT_DIADEMA_TYPES.register("csdy_world", () -> ClientDiademaType.Create(CsdyWorldClientDiadema::new));

    public static final RegistryObject<ClientDiademaType> MUSIC_GAME =
            CLIENT_DIADEMA_TYPES.register("music_game", () -> ClientDiademaType.Create(MusicGameClientDiadema::new));

}
