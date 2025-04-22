package com.csdy.jzyy.modifier.register;

import com.csdy.jzyy.modifier.modifier.*;
import com.csdy.jzyy.modifier.modifier.Severance.AbsoluteSeverance;
import com.csdy.jzyy.modifier.modifier.Severance.BaseSeveranceModifier;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.AncientOcean;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.DeepOceanBlessings;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.DeepOceanEcho;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.TheProphecyOfTheSunk;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.armor.DeepOceanProtect;
import com.csdy.jzyy.modifier.modifier.alex_mob.Kangaroo;
import com.csdy.jzyy.modifier.modifier.alex_mob.armor.VoidWalk;
import com.csdy.jzyy.modifier.modifier.blade_release.BladeRelease1;
import com.csdy.jzyy.modifier.modifier.blade_release.BladeRelease2;
import com.csdy.jzyy.modifier.modifier.blade_release.BladeRelease3;
import com.csdy.jzyy.modifier.modifier.csdy.CsdyArmor;
import com.csdy.jzyy.modifier.modifier.csdy.CsdyAttack;
import com.csdy.jzyy.modifier.modifier.csdy.CsdyWhisper;
import com.csdy.jzyy.modifier.modifier.etsh.FleshGear;
import com.csdy.jzyy.modifier.modifier.etsh.MindGear;
import com.csdy.jzyy.modifier.modifier.etsh.SpiritGear;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.*;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.armor.*;
import com.csdy.jzyy.modifier.modifier.jzyy.ChaosTransmigration;
import com.csdy.jzyy.modifier.modifier.jzyy.HornToss;
import com.csdy.jzyy.modifier.modifier.primal_reversion.*;
import com.csdy.jzyy.modifier.modifier.primal_reversion.armor.HaoransCult;
import com.csdy.jzyy.modifier.modifier.primal_reversion.armor.LifeStealArmor;
import com.csdy.jzyy.modifier.modifier.yue_zheng_ling.*;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

import static com.csdy.jzyy.ModMain.MODID;

public class ModifierRegister {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(MODID);
    ///弱小词条
    public static final StaticModifier<EdgeRunner> EDGE_RUNNER_STATIC_MODIFIER = MODIFIERS.register("edge_runner", EdgeRunner::new);
    public static final StaticModifier<BladeRelease1> BLADE_RELEASE1_STATIC_MODIFIER = MODIFIERS.register("blade_release1", BladeRelease1::new);
    public static final StaticModifier<BladeRelease2> BLADE_RELEASE2_STATIC_MODIFIER = MODIFIERS.register("blade_release2", BladeRelease2::new);
    public static final StaticModifier<BladeRelease3> BLADE_RELEASE3_STATIC_MODIFIER = MODIFIERS.register("blade_release3", BladeRelease3::new);
    public static final StaticModifier<AssAttack> ASS_ATTACK_STATIC_MODIFIER = MODIFIERS.register("ass_attack", AssAttack::new);
    public static final StaticModifier<HellFire> HELL_FIRE_STATIC_MODIFIER = MODIFIERS.register("hell_fire", HellFire::new);
    public static final StaticModifier<MythicalPush> MYTHICAL_PUSH_STATIC_MODIFIER = MODIFIERS.register("mythical_push", MythicalPush::new);
    public static final StaticModifier<ChaosTransmigration> CHAOS_TRANSMIGRATION_STATIC_MODIFIER = MODIFIERS.register("chaos_transmigration", ChaosTransmigration::new);
    public static final StaticModifier<HornToss> HORN_TOSS_STATIC_MODIFIER = MODIFIERS.register("horn_toss", HornToss::new);

    ///分割线
    public static final StaticModifier<Test> TEST_STATIC_MODIFIER = MODIFIERS.register("test", Test::new);
    public static final StaticModifier<Marisa> MARISA_STATIC_MODIFIER = MODIFIERS.register("marisa", Marisa::new);
    ///石墨砖
    public static final StaticModifier<Singer> SINGER_STATIC_MODIFIER = MODIFIERS.register("singer", Singer::new);
    public static final StaticModifier<Echo> ECHO_STATIC_MODIFIER = MODIFIERS.register("echo", Echo::new);
    public static final StaticModifier<SummerNight> SUMMER_NIGHT_STATIC_MODIFIER = MODIFIERS.register("summer_night", SummerNight::new);
    public static final StaticModifier<Cumulonimbus> CUMULONIMBUS_STATIC_MODIFIER = MODIFIERS.register("cumulonimbus", Cumulonimbus::new);
    public static final StaticModifier<ConvergentWorldline> CONVERGENT_WORLDLINE_STATIC_MODIFIER = MODIFIERS.register("convergent_worldline", ConvergentWorldline::new);
    public static final StaticModifier<ShatteredDream> SHATTERED_DREAM_STATIC_MODIFIER = MODIFIERS.register("shattered_dream", ShatteredDream::new);
    public static final StaticModifier<ColdRain> COLD_RAIN_STATIC_MODIFIER = MODIFIERS.register("cold_rain", ColdRain::new);
    public static final StaticModifier<SummerWind> SUMMER_WIND_STATIC_MODIFIER = MODIFIERS.register("summer_wind", SummerWind::new);
    public static final StaticModifier<HelloAndByeDays> HELLO_AND_BYE_DAYS_STATIC_MODIFIER = MODIFIERS.register("hello_bye_days", HelloAndByeDays::new);
    ///海渊
    public static final StaticModifier<AncientOcean> ANCIENT_OCEAN_STATIC_MODIFIER = MODIFIERS.register("ancient_ocean", AncientOcean::new);
    public static final StaticModifier<DeepOceanBlessings> DEEP_OCEAN_BLESSINGS_STATIC_MODIFIER = MODIFIERS.register("deep_ocean_blessing", DeepOceanBlessings::new);
    public static final StaticModifier<TheProphecyOfTheSunk> THE_PROPHECY_OF_THE_SUNK_STATIC_MODIFIER = MODIFIERS.register("the_prophecy_of_the_sunk", TheProphecyOfTheSunk::new);
    public static final StaticModifier<DeepOceanEcho> DEEP_OCEAN_ECHO_STATIC_MODIFIER = MODIFIERS.register("deep_ocean_echo", DeepOceanEcho::new);
    public static final StaticModifier<NoLevelsModifier> HUSH_STATIC_MODIFIER = MODIFIERS.register("hush", NoLevelsModifier::new);
    public static final StaticModifier<DeepOceanProtect> DEEP_OCEAN_PROTECT_STATIC_MODIFIER = MODIFIERS.register("deep_ocean_protect", DeepOceanProtect::new);
    ///魔力铀
    public static final StaticModifier<ClearBody> CLEAR_BODY_STATIC_MODIFIER = MODIFIERS.register("clear_body", ClearBody::new);
    ///原始回归相关
    public static final StaticModifier<Elemental> ELEMENTAL_STATIC_MODIFIER = MODIFIERS.register("elemental", Elemental::new);
    public static final StaticModifier<Terrafirma> TERRAFIRMA_STATIC_MODIFIER = MODIFIERS.register("terrafirma", Terrafirma::new);
    public static final StaticModifier<TradeOff> TRADE_OFF_STATIC_MODIFIER = MODIFIERS.register("trade_off", TradeOff::new);
    public static final StaticModifier<LifeSteal> LIFE_STEAL_STATIC_MODIFIER = MODIFIERS.register("life_steal", LifeSteal::new);
    public static final StaticModifier<LifeStealArmor> LIFE_STEAL_ARMOR_STATIC_MODIFIER = MODIFIERS.register("life_steal_armor", LifeStealArmor::new);
    public static final StaticModifier<BloodMary> BLOOD_MARY_STATIC_MODIFIER = MODIFIERS.register("blood_mary", BloodMary::new);
    public static final StaticModifier<HaoransCult> HAORANS_CULT_STATIC_MODIFIER = MODIFIERS.register("haorans_cult", HaoransCult::new);


    ///alex生物
    public static final StaticModifier<Kangaroo> KANGAROO_STATIC_MODIFIER = MODIFIERS.register("kangaroo", Kangaroo::new);
    public static final StaticModifier<VoidWalk> VOID_WALK_STATIC_MODIFIER = MODIFIERS.register("void_walk", VoidWalk::new);


    ///强大词条

    public static final StaticModifier<ImagineBreaker> IMAGINE_BREAKER_STATIC_MODIFIER = MODIFIERS.register("imagine_breaker", ImagineBreaker::new);


    ///切断及换皮
    public static final StaticModifier<BaseSeveranceModifier> SEVERANCE_STATIC_MODIFIER = MODIFIERS.register("severance", () -> new BaseSeveranceModifier(0.1F));

    public static final StaticModifier<BaseSeveranceModifier> CLEAVE_THE_STARS = MODIFIERS.register("cleave_the_stars", () -> new BaseSeveranceModifier(0.05F));
    public static final StaticModifier<BaseSeveranceModifier> DRAGON_POWER_STATIC_MODIFIER = MODIFIERS.register("dragon_power", () -> new BaseSeveranceModifier(0.1F));
    public static final StaticModifier<BaseSeveranceModifier> VOID_TOUCH_STATIC_MODIFIER = MODIFIERS.register("void_touch", () -> new BaseSeveranceModifier(0.2F));


    ///绝对切断及换皮
    public static final StaticModifier<AbsoluteSeverance> ABSOLUTE_SEVERANCE_STATIC_MODIFIER = MODIFIERS.register("absolute_severance", () -> new AbsoluteSeverance(0.5F));
    public static final StaticModifier<Cosmos> COSMOS_STATIC_MODIFIER = MODIFIERS.register("cosmos",() -> new Cosmos(1F));

    ///冰火

    public static final StaticModifier<DragonFire> DRAGON_FIRE_STATIC_MODIFIER = MODIFIERS.register("dragon_fire", DragonFire::new);
    public static final StaticModifier<DragonIce> DRAGON_ICE_STATIC_MODIFIER = MODIFIERS.register("dragon_ice", DragonIce::new);
    public static final StaticModifier<DragonFlash> DRAGON_FLASH_STATIC_MODIFIER = MODIFIERS.register("dragon_flash", DragonFlash::new);

    public static final StaticModifier<FireDragonArmor> FIRE_DRAGON_ARMOR_STATIC_MODIFIER = MODIFIERS.register("fire_dragon_armor", FireDragonArmor::new);
    public static final StaticModifier<IceDragonArmor> ICE_DRAGON_ARMOR_STATIC_MODIFIER = MODIFIERS.register("ice_dragon_armor", IceDragonArmor::new);
    public static final StaticModifier<FlashDragonArmor> FLASH_DRAGON_ARMOR_STATIC_MODIFIER = MODIFIERS.register("flash_dragon_armor", FlashDragonArmor::new);

    ///etsh
    public static final StaticModifier<SpiritGear> SPIRIT_GEAR_STATIC_MODIFIER = MODIFIERS.register("spirit_gear", SpiritGear::new);
    public static final StaticModifier<MindGear> MIND_GEAR_STATIC_MODIFIER = MODIFIERS.register("mind_gear", MindGear::new);
    public static final StaticModifier<FleshGear> FLESH_GEAR_STATIC_MODIFIER = MODIFIERS.register("flesh_gear", FleshGear::new);

    ///csdy
    public static final StaticModifier<FuckMemoryTillDie> FUCK_MEMORY_TILL_DIE_STATIC_MODIFIER = MODIFIERS.register("fuck_memory_till_die", FuckMemoryTillDie::new);
    public static final StaticModifier<CsdyArmor> CSDY_ARMOR_STATIC_MODIFIER = MODIFIERS.register("csdy_armor", CsdyArmor::new);
    public static final StaticModifier<CsdyAttack> CSDY_ATTACK_STATIC_MODIFIER = MODIFIERS.register("csdy_attack", CsdyAttack::new);
    public static final StaticModifier<CsdyWhisper> CSDY_WHISPER_STATIC_MODIFIER = MODIFIERS.register("csdy_whisper", CsdyWhisper::new);



}
