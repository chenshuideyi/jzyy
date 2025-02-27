package com.csdy.diadema;

import com.csdy.ModMain;
import com.csdy.diadema.abyss.AbyssDiadema;
import com.csdy.diadema.abyss.AbyssClientDiadema;
import com.csdy.diadema.meridiaVerse.MeridiaVerseClientDiadema;
import com.csdy.diadema.meridiaVerse.MeridiaVerseDiadema;
import com.csdy.diadema.warden.WardenClientDiadema;
import com.csdy.diadema.warden.WardenDiadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.CsdyRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

// 把你的领域注册上来就算是完成了！
public class DiademaRegister {
    public static final DeferredRegister<DiademaType> DIADEMA_TYPES = DeferredRegister.create(CsdyRegistries.DIADEMA_TYPE, ModMain.MODID);

    public static final RegistryObject<DiademaType> WARDEN =
            DIADEMA_TYPES.register("warden", () -> DiademaType.Create(WardenDiadema::new, WardenClientDiadema::new));

    public static final RegistryObject<DiademaType> MERIDIA_VERSE =
            DIADEMA_TYPES.register("meridia_verse", () -> DiademaType.Create(MeridiaVerseDiadema::new, MeridiaVerseClientDiadema::new));
    public static final RegistryObject<DiademaType> ABYSS =
            DIADEMA_TYPES.register("abyss", () -> DiademaType.Create(AbyssDiadema::new, AbyssClientDiadema::new));
}
