package com.csdy.modifier.register;

import com.csdy.modifier.AuraFormaModifier;
import com.csdy.modifier.FormaModifier;
import com.csdy.modifier.diadema.*;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

import static com.csdy.ModMain.MODID;

public class ModifierRegister {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(MODID);
    public static final StaticModifier<AuraFormaModifier> AURA_FORMA_MODIFIER_STATIC_MODIFIER = MODIFIERS.register("aura_forma", AuraFormaModifier::new);
    public static final StaticModifier<FormaModifier> FORMA_MODIFIER_STATIC_MODIFIER = MODIFIERS.register("forma", FormaModifier::new);
    public static final StaticModifier<Gula> GULA_STATIC_MODIFIER = MODIFIERS.register("gula", Gula::new);
    static final StaticModifier<FakeMeltDown> FAKE_MELT_DOWN_STATIC_MODIFIER = MODIFIERS.register("fake_meltdown", FakeMeltDown::new);
    public static final StaticModifier<MeltDown> MELT_DOWN_STATIC_MODIFIER = MODIFIERS.register("meltdown", MeltDown::new);
    public static final StaticModifier<Luxuria> LUXURIA_STATIC_MODIFIER = MODIFIERS.register("luxuria", Luxuria::new);
    public static final StaticModifier<Warden> WARDEN_STATIC_MODIFIER = MODIFIERS.register("warden", Warden::new);
    public static final StaticModifier<FakeKillAura> FAKE_KILL_AURA_STATIC_MODIFIER = MODIFIERS.register("fake_kill_aura", FakeKillAura::new);
    public static final StaticModifier<KillAura> KILL_AURA_STATIC_MODIFIER = MODIFIERS.register("kill_aura", KillAura::new);
    public static final StaticModifier<FakeProjectE> FAKE_PROJECT_E_STATIC_MODIFIER = MODIFIERS.register("fake_projecte", FakeProjectE::new);
    public static final StaticModifier<ProjectE> PROJECT_E_STATIC_MODIFIER = MODIFIERS.register("projecte", ProjectE::new);
    public static final StaticModifier<Wind> WIND_STATIC_MODIFIER = MODIFIERS.register("wind", Wind::new);
    public static final StaticModifier<MeridiaVerse> MERIDIA_VERSE_STATIC_MODIFIER = MODIFIERS.register("meridia_verse", MeridiaVerse::new);
    public static final StaticModifier<Avarita> AVARITA_STATIC_MODIFIER = MODIFIERS.register("avarita", Avarita::new);
    public static final StaticModifier<Superbia> SUPERBIA_STATIC_MODIFIER = MODIFIERS.register("superbia", Superbia::new);
    public static final StaticModifier<Abyss> ABYSS_STATIC_MODIFIER = MODIFIERS.register("abyss", Abyss::new);







}
