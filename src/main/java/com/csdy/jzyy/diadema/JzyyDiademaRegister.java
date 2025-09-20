package com.csdy.jzyy.diadema;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.diadema.csdyworld.CsdyWorldDiadema;
import com.csdy.jzyy.diadema.meltdown.LuckyTntMeltdownDiadema;
import com.csdy.jzyy.diadema.miziao.MusicGameDiadema;
import com.csdy.tcondiadema.frames.CsdyRegistries;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JzyyDiademaRegister {
    public static final DeferredRegister<DiademaType> DIADEMA_TYPES = DeferredRegister.create(CsdyRegistries.DIADEMA_TYPE, JzyyModMain.MODID);

    public static final RegistryObject<DiademaType> LUCKY_TNT_MELTDOWN =
            DIADEMA_TYPES.register("lucky_tnt_meltdown", () -> DiademaType.create(LuckyTntMeltdownDiadema::new));

    public static final RegistryObject<DiademaType> CSDY_WORLD =
            DIADEMA_TYPES.register("csdy_world", () -> DiademaType.create(CsdyWorldDiadema::new));

    public static final RegistryObject<DiademaType> MUSIC_GAME =
            DIADEMA_TYPES.register("music_game", () -> DiademaType.create(MusicGameDiadema::new));

}
