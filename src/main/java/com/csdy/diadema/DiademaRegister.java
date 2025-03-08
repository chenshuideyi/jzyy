package com.csdy.diadema;

import com.csdy.ModMain;
import com.csdy.diadema.abyss.AbyssDiadema;
import com.csdy.diadema.abyss.AbyssClientDiadema;
import com.csdy.diadema.avaritia.AvaritaClientDiadema;
import com.csdy.diadema.avaritia.AvaritaDiadema;
import com.csdy.diadema.fakekillaura.FakeKillAuraClientDiadema;
import com.csdy.diadema.fakekillaura.FakeKillAuraDiadema;
import com.csdy.diadema.fakemeltdown.FakeMeltdownClientDiadema;
import com.csdy.diadema.fakemeltdown.FakeMeltdownDiadema;
import com.csdy.diadema.fakeprojecte.FakeProjectEClientDiadema;
import com.csdy.diadema.fakeprojecte.FakeProjectEDiadema;
import com.csdy.diadema.gula.GulaClientDiadema;
import com.csdy.diadema.gula.GulaDiadema;
import com.csdy.diadema.killaura.KillAuraClientDiadema;
import com.csdy.diadema.killaura.KillAuraDiadema;
import com.csdy.diadema.luxuria.LuxuriaClinetDiadema;
import com.csdy.diadema.luxuria.LuxuriaDiadema;
import com.csdy.diadema.meltdown.MeltdownClientDiadema;
import com.csdy.diadema.meltdown.MeltdownDiadema;
import com.csdy.diadema.meridiaVerse.MeridiaVerseClientDiadema;
import com.csdy.diadema.meridiaVerse.MeridiaVerseDiadema;
import com.csdy.diadema.projecte.ProjectEClientDiadema;
import com.csdy.diadema.projecte.ProjectEDiadema;
import com.csdy.diadema.warden.WardenClientDiadema;
import com.csdy.diadema.warden.WardenDiadema;
import com.csdy.diadema.wind.WindClientDiadema;
import com.csdy.diadema.wind.WindDiadema;
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
    public static final RegistryObject<DiademaType> WIND =
            DIADEMA_TYPES.register("wind", () -> DiademaType.Create(WindDiadema::new, WindClientDiadema::new));
    public static final RegistryObject<DiademaType> GULA =
            DIADEMA_TYPES.register("gula", () -> DiademaType.Create(GulaDiadema::new, GulaClientDiadema::new));
    public static final RegistryObject<DiademaType> LUXURIA =
            DIADEMA_TYPES.register("luxuria", () -> DiademaType.Create(LuxuriaDiadema::new, LuxuriaClinetDiadema::new));
    public static final RegistryObject<DiademaType> KILL_AURA =
            DIADEMA_TYPES.register("kill_aura", () -> DiademaType.Create(KillAuraDiadema::new, KillAuraClientDiadema::new));
    public static final RegistryObject<DiademaType> FAKE_KILL_AURA =
            DIADEMA_TYPES.register("fake_kill_aura", () -> DiademaType.Create(FakeKillAuraDiadema::new, FakeKillAuraClientDiadema::new));
    public static final RegistryObject<DiademaType> PROJECTE =
            DIADEMA_TYPES.register("projecte", () -> DiademaType.Create(ProjectEDiadema::new, ProjectEClientDiadema::new));
    public static final RegistryObject<DiademaType> FAKE_PROJECTE =
            DIADEMA_TYPES.register("fake_projecte", () -> DiademaType.Create(FakeProjectEDiadema::new, FakeProjectEClientDiadema::new));
    public static final RegistryObject<DiademaType> MELT_DOWN =
            DIADEMA_TYPES.register("meltdown", () -> DiademaType.Create(MeltdownDiadema::new, MeltdownClientDiadema::new));
    public static final RegistryObject<DiademaType> FAKE_MELT_DOWN =
            DIADEMA_TYPES.register("fake_meltdown", () -> DiademaType.Create(FakeMeltdownDiadema::new, FakeMeltdownClientDiadema::new));
    public static final RegistryObject<DiademaType> AVARITA =
            DIADEMA_TYPES.register("avarita", () -> DiademaType.Create(AvaritaDiadema::new, AvaritaClientDiadema::new));

}
