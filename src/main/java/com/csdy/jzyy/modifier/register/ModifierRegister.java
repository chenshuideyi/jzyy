package com.csdy.jzyy.modifier.register;

import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.modifier.modifier.*;
import com.csdy.jzyy.modifier.modifier.Severance.AbsoluteSeverance;
import com.csdy.jzyy.modifier.modifier.Severance.BaseSeveranceModifier;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.AncientOcean;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.DeepOceanBlessings;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.DeepOceanEcho;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.TheProphecyOfTheSunk;
import com.csdy.jzyy.modifier.modifier.abyss_alloy.armor.DeepOceanProtect;
import com.csdy.jzyy.modifier.modifier.alex_mob.armor.Kangaroo;
import com.csdy.jzyy.modifier.modifier.alex_mob.armor.VoidWalk;
import com.csdy.jzyy.modifier.modifier.bedrock.BedRock;
import com.csdy.jzyy.modifier.modifier.bedrock.Deicide;
import com.csdy.jzyy.modifier.modifier.bedrock.Mouse3;
import com.csdy.jzyy.modifier.modifier.bian.*;
import com.csdy.jzyy.modifier.modifier.blade_release.BladeRelease1;
import com.csdy.jzyy.modifier.modifier.blade_release.BladeRelease2;
import com.csdy.jzyy.modifier.modifier.blade_release.BladeRelease3;
import com.csdy.jzyy.modifier.modifier.csdy.CsdyArmor;
import com.csdy.jzyy.modifier.modifier.csdy.CsdyAttack;
import com.csdy.jzyy.modifier.modifier.csdy.CsdyWhisper;
import com.csdy.jzyy.modifier.modifier.csdytinker.*;
import com.csdy.jzyy.modifier.modifier.dx.BaseToolMusic;
import com.csdy.jzyy.modifier.modifier.dx.Music;
import com.csdy.jzyy.modifier.modifier.dx.diadema.AwakenPandora;
import com.csdy.jzyy.modifier.modifier.dx.tool.AwakenTiamat;
import com.csdy.jzyy.modifier.modifier.ender.SaberExcalibur;
import com.csdy.jzyy.modifier.modifier.ender.TrueNameLiberation;
import com.csdy.jzyy.modifier.modifier.etsh.FleshGear;
import com.csdy.jzyy.modifier.modifier.etsh.MindGear;
import com.csdy.jzyy.modifier.modifier.etsh.SpiritGear;
import com.csdy.jzyy.modifier.modifier.experience.ExperienceArmor;
import com.csdy.jzyy.modifier.modifier.experience.ExperienceKiller;
import com.csdy.jzyy.modifier.modifier.experience.real.RealExperienceArmor;
import com.csdy.jzyy.modifier.modifier.experience.real.RealExperienceKiller;
import com.csdy.jzyy.modifier.modifier.glass.Loupe;
import com.csdy.jzyy.modifier.modifier.glass.Prism;
import com.csdy.jzyy.modifier.modifier.hallowed_bar.HolyProtection;
import com.csdy.jzyy.modifier.modifier.harcadium.EatStone;
import com.csdy.jzyy.modifier.modifier.harcadium.HarcadiumArmor;
import com.csdy.jzyy.modifier.modifier.ice.HyperBeam;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.DragonBone;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.DragonFire;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.DragonFlash;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.DragonIce;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.armor.FireDragonArmor;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.armor.FlashDragonArmor;
import com.csdy.jzyy.modifier.modifier.ice_and_fire.armor.IceDragonArmor;
import com.csdy.jzyy.modifier.modifier.jzyy.*;
import com.csdy.jzyy.modifier.modifier.jzyy.sponsor.Ciallo;
import com.csdy.jzyy.modifier.modifier.jzyy.sponsor.FinalSword;
import com.csdy.jzyy.modifier.modifier.living_wood.Ecological;
import com.csdy.jzyy.modifier.modifier.living_wood.real.ForestAngry;
import com.csdy.jzyy.modifier.modifier.living_wood.real.ForestArmor;
import com.csdy.jzyy.modifier.modifier.living_wood.real.Life;
import com.csdy.jzyy.modifier.modifier.ly.armor.Return;
import com.csdy.jzyy.modifier.modifier.pla_steel.Crystalline;
import com.csdy.jzyy.modifier.modifier.primal_reversion.*;
import com.csdy.jzyy.modifier.modifier.primal_reversion.armor.HaoransCult;
import com.csdy.jzyy.modifier.modifier.primal_reversion.armor.LifeStealArmor;
import com.csdy.jzyy.modifier.modifier.projectE.EquivalentEdge;
import com.csdy.jzyy.modifier.modifier.projectE.Juggernaut;
import com.csdy.jzyy.modifier.modifier.projectE.armor.EquivalentArmor;
import com.csdy.jzyy.modifier.modifier.projectE.armor.Ultradense;
import com.csdy.jzyy.modifier.modifier.pufferfish.QuillSpray;
import com.csdy.jzyy.modifier.modifier.pufferfish.Stinger;
import com.csdy.jzyy.modifier.modifier.rainbow.ColorModifier;
import com.csdy.jzyy.modifier.modifier.real_form.RealEgo;
import com.csdy.jzyy.modifier.modifier.real_form.RealExperience;
import com.csdy.jzyy.modifier.modifier.real_form.RealLivingWood;
import com.csdy.jzyy.modifier.modifier.real_form.RealRenstoneComponent;
import com.csdy.jzyy.modifier.modifier.redstone_component.Precision;
import com.csdy.jzyy.modifier.modifier.redstone_component.real.Disruptor;
import com.csdy.jzyy.modifier.modifier.redstone_component.real.FullOfEnergy;
import com.csdy.jzyy.modifier.modifier.silence.SilenceAlmighty;
import com.csdy.jzyy.modifier.modifier.srp.EnergyRecovery;
import com.csdy.jzyy.modifier.modifier.tong_ban.TongGreed;
import com.csdy.jzyy.modifier.modifier.warframe1999.Modifier1999;
import com.csdy.jzyy.modifier.modifier.warframe1999.ModifierArmor1999;
import com.csdy.jzyy.modifier.modifier.warframe1999.armor.Mag;
import com.csdy.jzyy.modifier.modifier.warframe1999.armor.Nyx;
import com.csdy.jzyy.modifier.modifier.warframe1999.armor.Trinity;
import com.csdy.jzyy.modifier.modifier.warframe1999.tool.Cyte09;
import com.csdy.jzyy.modifier.modifier.warframe1999.tool.Excalibur;
import com.csdy.jzyy.modifier.modifier.warframe1999.tool.Volt;
import com.csdy.jzyy.modifier.modifier.yue_zheng_ling.*;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.modifier.CommonDiademaModifier;
import com.csdy.tcondiadema.modifier.DiademaModifier;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

import static com.csdy.jzyy.JzyyModMain.MODID;

public class ModifierRegister {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(MODID);

    ///工具专属
    public static final StaticModifier<MagicFlying> MAGIC_FLYING_STATIC_MODIFIER = MODIFIERS.register("magic_flying", MagicFlying::new);







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

    ///黑魔导冰清
    public static final StaticModifier<HyperBeam> HYPER_BEAM_STATIC_MODIFIER = MODIFIERS.register("hyper_beam", HyperBeam::new);


    ///桐
    public static final StaticModifier<TongGreed> TONG_GREED_STATIC_MODIFIER = MODIFIERS.register("tong_greed", TongGreed::new);

    /// 记得上色
    public static final StaticModifier<EquivalentEdge> EQUIVALENT_EDGE_STATIC_MODIFIER = MODIFIERS.register("equivalent_edge", EquivalentEdge::new);
    public static final StaticModifier<EquivalentArmor> EQUIVALENT_ARMOR_STATIC_MODIFIER = MODIFIERS.register("equivalent_armor", EquivalentArmor::new);
    public static final StaticModifier<Ultradense> ULTRADENSE_STATIC_MODIFIER = MODIFIERS.register("ultradense", Ultradense::new);
    public static final StaticModifier<Juggernaut> JUGGERNAUT_STATIC_MODIFIER = MODIFIERS.register("juggernaut", Juggernaut::new);

    ///玻璃钢
    public static final StaticModifier<Crystalline> CRYSTALLINE_STATIC_MODIFIER = MODIFIERS.register("crystalline", Crystalline::new);




    ///alex生物
    public static final StaticModifier<Kangaroo> KANGAROO_STATIC_MODIFIER = MODIFIERS.register("kangaroo", Kangaroo::new);
    public static final StaticModifier<VoidWalk> VOID_WALK_STATIC_MODIFIER = MODIFIERS.register("void_walk", VoidWalk::new);


    ///强大词条

    public static final StaticModifier<LastSword> LAST_SWORD_STATIC_MODIFIER = MODIFIERS.register("last_sword", LastSword::new);
    public static final StaticModifier<ExpediteSuffering> EXPEDITE_SUFFERING_STATIC_MODIFIER = MODIFIERS.register("expedite_suffering", ExpediteSuffering::new);
    public static final StaticModifier<ImagineBreaker> IMAGINE_BREAKER_STATIC_MODIFIER = MODIFIERS.register("imagine_breaker", ImagineBreaker::new);
    public static final StaticModifier<EchoForm> ECHO_FORM_STATIC_MODIFIER = MODIFIERS.register("echo_form", EchoForm::new);
    public static final StaticModifier<NoLevelsModifier> ECHO_FORM1_STATIC_MODIFIER = MODIFIERS.register("echo_form1", NoLevelsModifier::new);
    public static final StaticModifier<Infinitum> INFINITUM_STATIC_MODIFIER = MODIFIERS.register("infinitum", Infinitum::new);


    ///哈肯典姆
    public static final StaticModifier<EatStone> EAT_STONE_STATIC_MODIFIER = MODIFIERS.register("eat_stone", EatStone::new);
    public static final StaticModifier<NoLevelsModifier> BEDROCK_BREAK = MODIFIERS.register("bedrock_breaker", NoLevelsModifier::new);
    public static final StaticModifier<HarcadiumArmor> HARCADIUM_ARMOR_STATIC_MODIFIER = MODIFIERS.register("harcadium_armor", HarcadiumArmor::new);

    ///基岩
    public static final StaticModifier<BedRock> BED_ROCK_STATIC_MODIFIER = MODIFIERS.register("bedrock", BedRock::new);
    public static final StaticModifier<Deicide> DEICIDE_STATIC_MODIFIER = MODIFIERS.register("deicide", Deicide::new);
    public static final StaticModifier<Mouse3> MOUSE_3_STATIC_MODIFIER = MODIFIERS.register("mouse3", Mouse3::new);
    ///假的
    public static final StaticModifier<NoLevelsModifier> JUST_BED_AND_ROCK = MODIFIERS.register("just_bed_and_rock", NoLevelsModifier::new);

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

    public static final StaticModifier<DragonBone> DRAGON_BONE_STATIC_MODIFIER = MODIFIERS.register("dragon_bone", DragonBone::new);

    ///ender
    public static final StaticModifier<SaberExcalibur> SABER_EXCALIBUR_STATIC_MODIFIER = MODIFIERS.register("excalibur", SaberExcalibur::new);



    ///米子山民
    public static final StaticModifier<Music> MUSIC_STATIC_MODIFIER = MODIFIERS.register("music", Music::new);

    ///这里是工具的音乐
    public static final StaticModifier<BaseToolMusic> AMA_NO_JYAKU = MODIFIERS.register("ama_no_jyaku", () -> new BaseToolMusic(JzyySoundsRegister.AMA_NO_JYAKU.get()));
    public static final StaticModifier<BaseToolMusic> MONSTER_SONG = MODIFIERS.register("monster_song", () -> new BaseToolMusic(JzyySoundsRegister.MONSTER_SONG.get()));
    public static final StaticModifier<BaseToolMusic> SIX_TRILLION_YEARS = MODIFIERS.register("six_trillion_years", () -> new BaseToolMusic(JzyySoundsRegister.SIX_TRILLION_YEARS.get()));
    public static final StaticModifier<BaseToolMusic> THERMAL_ANOMALY = MODIFIERS.register("thermal_anomaly", () -> new BaseToolMusic(JzyySoundsRegister.THERMAL_ANOMALY.get()));
    public static final StaticModifier<BaseToolMusic> I_LOVE_YOU = MODIFIERS.register("i_love_u", () -> new BaseToolMusic(JzyySoundsRegister.I_LOVE_U.get()));
    public static final StaticModifier<BaseToolMusic> THE_MILLENNIUM_SNOW = MODIFIERS.register("the_millennium_snow", () -> new BaseToolMusic(JzyySoundsRegister.THE_MILLENNIUM_SNOW.get()));
    public static final StaticModifier<BaseToolMusic> ONLY_WISH = MODIFIERS.register("only_wish", () -> new BaseToolMusic(JzyySoundsRegister.ONLY_WISH.get()));
    public static final StaticModifier<BaseToolMusic> GIRL_A = MODIFIERS.register("girl_a", () -> new BaseToolMusic(JzyySoundsRegister.GIRL_A.get()));
    public static final StaticModifier<BaseToolMusic> UMIYURI_KAITEITAN = MODIFIERS.register("umiyuri_kaiteitan", () -> new BaseToolMusic(JzyySoundsRegister.UMIYURI_KAITEITAN.get()));



    public static final StaticModifier<BaseToolMusic> TIAMAT = MODIFIERS.register("tiamat", () -> new BaseToolMusic(JzyySoundsRegister.TIAMAT.get()));
    public static final StaticModifier<BaseToolMusic> PANDORA = MODIFIERS.register("pandora", () -> new BaseToolMusic(JzyySoundsRegister.PANDORA.get()));



    ///有效果的音乐
    public static final StaticModifier<AwakenPandora> AWAKEN_PANDORA_STATIC_MODIFIER = MODIFIERS.register("awaken_pandora", AwakenPandora::new);
    public static final StaticModifier<AwakenTiamat> AWAKEN_TIAMAT_STATIC_MODIFIER = MODIFIERS.register("awaken_tiamat", AwakenTiamat::new);

    ///玻璃
    public static final StaticModifier<Loupe> LOUPE_STATIC_MODIFIER = MODIFIERS.register("loupe", Loupe::new);
    public static final StaticModifier<Prism> PRISM_STATIC_MODIFIER = MODIFIERS.register("prism", Prism::new);

    ///河豚
    public static final StaticModifier<Stinger> STINGER_STATIC_MODIFIER = MODIFIERS.register("stinger", Stinger::new);
    public static final StaticModifier<QuillSpray> QUILL_SPRAY_STATIC_MODIFIER = MODIFIERS.register("quill_spray", QuillSpray::new);

    ///能量回收
    public static final StaticModifier<EnergyRecovery> ENERGY_RECOVERY_STATIC_MODIFIER = MODIFIERS.register("energy_recovery", EnergyRecovery::new);

    ///etsh
    public static final StaticModifier<SpiritGear> SPIRIT_GEAR_STATIC_MODIFIER = MODIFIERS.register("spirit_gear", SpiritGear::new);
    public static final StaticModifier<MindGear> MIND_GEAR_STATIC_MODIFIER = MODIFIERS.register("mind_gear", MindGear::new);
    public static final StaticModifier<FleshGear> FLESH_GEAR_STATIC_MODIFIER = MODIFIERS.register("flesh_gear", FleshGear::new);

    ///csdy
    public static final StaticModifier<FuckMemoryTillDie> FUCK_MEMORY_TILL_DIE_STATIC_MODIFIER = MODIFIERS.register("fuck_memory_till_die", FuckMemoryTillDie::new);
    public static final StaticModifier<CsdyArmor> CSDY_ARMOR_STATIC_MODIFIER = MODIFIERS.register("csdy_armor", CsdyArmor::new);
    public static final StaticModifier<CsdyAttack> CSDY_ATTACK_STATIC_MODIFIER = MODIFIERS.register("csdy_attack", CsdyAttack::new);
    public static final StaticModifier<CsdyWhisper> CSDY_WHISPER_STATIC_MODIFIER = MODIFIERS.register("csdy_whisper", CsdyWhisper::new);

    ///csdytinker
    public static final StaticModifier<ScarySky> SCARY_SKY_STATIC_MODIFIER = MODIFIERS.register("scary_sky", ScarySky::new);
    public static final StaticModifier<TeleKill> TELE_KILL_STATIC_MODIFIER = MODIFIERS.register("tele_kill", TeleKill::new);
    public static final StaticModifier<Apartheid> APARTHEID_STATIC_MODIFIER = MODIFIERS.register("apartheid", Apartheid::new);
    public static final StaticModifier<CutOff> CUT_OFF_STATIC_MODIFIER = MODIFIERS.register("cut_off", CutOff::new);
    public static final StaticModifier<CoupDeGrace> COUP_DE_GRACE_STATIC_MODIFIER = MODIFIERS.register("coup_de_grace", CoupDeGrace::new);
    public static final StaticModifier<Kagerou> KAGEROU_STATIC_MODIFIER = MODIFIERS.register("kagerou", Kagerou::new);
    public static final StaticModifier<Pulverize> PULVERIZE_STATIC_MODIFIER = MODIFIERS.register("pulverize", Pulverize::new);

    ///雨幕
    public static final StaticModifier<Cyte09> CYTE_09_STATIC_MODIFIER = MODIFIERS.register("1999_quincy", Cyte09::new);
    public static final StaticModifier<Volt> VOLT_STATIC_MODIFIER = MODIFIERS.register("1999_amir", Volt::new);
    public static final StaticModifier<Excalibur> EXCALIBUR_STATIC_MODIFIER = MODIFIERS.register("1999_arthur", Excalibur::new);

    public static final StaticModifier<Mag> MAG_STATIC_MODIFIER = MODIFIERS.register("1999_aoi", Mag::new);
    public static final StaticModifier<Trinity> TRINITY_STATIC_MODIFIER = MODIFIERS.register("1999_leticia", Trinity::new);
    public static final StaticModifier<Nyx> NYX_STATIC_MODIFIER = MODIFIERS.register("1999_eleanor", Nyx::new);

    ///j村
    public static final StaticModifier<BitterRigidIce.slushing> SLUSHING_STATIC_MODIFIER = MODIFIERS.register("slushing", BitterRigidIce.slushing::new);
    public static final StaticModifier<GiantMonsterHair.frostabsorption> FROSTABSORPTION_STATIC_MODIFIER = MODIFIERS.register("frostabsorption", GiantMonsterHair.frostabsorption::new);
    public static final StaticModifier<LightningWormChitin.lightningabsorption> LIGHTNINGABSORPTION_STATIC_MODIFIER = MODIFIERS.register("lightningabsorption", LightningWormChitin.lightningabsorption::new);
    public static final StaticModifier<FireSecretorAdhesive.fireabsorption> FIRESECRETORADHESIVE_STATIC_MODIFIER = MODIFIERS.register("fireabsorption", FireSecretorAdhesive.fireabsorption::new);
    public static final StaticModifier<MalignasaurTeethArmor.ancientrespiration> MALIGNASAURTEETHARMOR_STATIC_MODIFIER = MODIFIERS.register("ancientrespiration", MalignasaurTeethArmor.ancientrespiration::new);
    public static final StaticModifier<MalignasaurTeeth.hungryhunter> MALIGNASAURTEETH_STATIC_MODIFIER = MODIFIERS.register("hungryhunter", MalignasaurTeeth.hungryhunter::new);
    public static final StaticModifier<MarineReptilesFineScale.eternalhunger> MARINEREPTILESFINESCALE_STATIC_MODIFIER = MODIFIERS.register("eternalhunger", MarineReptilesFineScale.eternalhunger::new);

    ///护甲补强
    public static final StaticModifier<Refraction> REFRACTION_STATIC_MODIFIER = MODIFIERS.register("refraction", Refraction::new);
    public static final StaticModifier<Love> LOVE_STATIC_MODIFIER = MODIFIERS.register("love", Love::new);


    ///真实形态
    public static final StaticModifier<RealExperience> REAL_EXPERIENCE_STATIC_MODIFIER =
            MODIFIERS.register(
                    "real_experience",
                    () -> new RealExperience(
                            "experience_steel",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "real_experience_steel"),
                                    "default"
                            ),
                            "经验钢达到了完美境界！"
                    )
            );

    public static final StaticModifier<RealLivingWood> REAL_LIVING_WOOD_STATIC_MODIFIER =
            MODIFIERS.register(
                    "real_living_wood",
                    () -> new RealLivingWood(
                            "living_wood",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "real_living_wood"),
                                    "default"
                            ),
                            "生命木在极端条件下突破了极限！"
                    )
            );

    public static final StaticModifier<RealRenstoneComponent> REAL_RENSTONE_COMPONENT_STATIC_MODIFIER =
            MODIFIERS.register(
                    "real_redstone_component",
                    () -> new RealRenstoneComponent(
                            "unpowered_redstone_component",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "real_redstone_component"),
                                    "default"
                            ),
                            "红石构件充能到了极致！"
                    )
            );



    public static final StaticModifier<TrueNameLiberation> TRUE_NAME_LIBERATION_STATIC_MODIFIER =
            MODIFIERS.register(
                    "true_name_liberation",
                    () -> new TrueNameLiberation(
                            "yakumo_ender",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "excalibar"),
                                    "default"
                            ),
                            "真名解放！"
                    )
            );

    public static final StaticModifier<RealEgo> REAL_EGO_STATIC_MODIFIER =
            MODIFIERS.register(
                    "real_ego",
                    () -> new RealEgo(
                            "unpowered_redstone_component",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "real_redstone_component"),
                                    "default"
                            ),
                            "E.G.O觉醒！"
                    )
            );

    public static final StaticModifier<Modifier1999> MODIFIER_1999_STATIC_MODIFIER =
            MODIFIERS.register(
                    "1999",
                    () -> new Modifier1999(
                            "rain_curtain",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "true_rain_curtain"),
                                    "default"
                            ),
                            "与六人组的一位取得了共鸣"
                    )
            );

    public static final StaticModifier<ModifierArmor1999> MODIFIER_ARMOR_1999_STATIC_MODIFIER =
            MODIFIERS.register(
                    "1999_armor",
                    () -> new ModifierArmor1999(
                            "rain_curtain",
                            MaterialVariantId.create(
                                    new MaterialId("jzyy", "true_rain_curtain"),
                                    "default"
                            ),
                            "与六人组的一位取得了共鸣"
                    )
            );


    ///经验钢
    public static final StaticModifier<ExperienceKiller> EXPERIENCE_KILLER_STATIC_MODIFIER = MODIFIERS.register("experience_killer", ExperienceKiller::new);
    public static final StaticModifier<RealExperienceKiller> REAL_EXPERIENCE_KILLER_STATIC_MODIFIER = MODIFIERS.register("real_experience_killer", RealExperienceKiller::new);
    public static final StaticModifier<ExperienceArmor> EXPERIENCE_ARMOR_STATIC_MODIFIER = MODIFIERS.register("experience_armor", ExperienceArmor::new);
    public static final StaticModifier<RealExperienceArmor> REAL_EXPERIENCE_ARMOR_STATIC_MODIFIER = MODIFIERS.register("real_experience_armor", RealExperienceArmor::new);

    ///生命木
    public static final StaticModifier<Ecological> ECOLOGICAL_STATIC_MODIFIER = MODIFIERS.register("ecological", Ecological::new);
    public static final StaticModifier<ForestAngry> FOREST_ANGRY_STATIC_MODIFIER = MODIFIERS.register("forest_angry", ForestAngry::new);
    public static final StaticModifier<ForestArmor> FOREST_ARMOR_STATIC_MODIFIER = MODIFIERS.register("forest_armor", ForestArmor::new);
    public static final StaticModifier<Life> LIFE_STATIC_MODIFIER = MODIFIERS.register("life", Life::new);

    ///红石构件
    public static final StaticModifier<Disruptor> DISRUPTOR_STATIC_MODIFIER = MODIFIERS.register("disruptor", Disruptor::new);
    public static final StaticModifier<FullOfEnergy> FULL_OF_ENERGY_STATIC_MODIFIER = MODIFIERS.register("full_of_energy", FullOfEnergy::new);
    public static final StaticModifier<Precision> PRECISION_STATIC_MODIFIER = MODIFIERS.register("precision", Precision::new);

    ///赞助强化这一块
    public static final StaticModifier<FinalSword> FINAL_SWORD_STATIC_MODIFIER = MODIFIERS.register("final_sword", FinalSword::new);
    public static final StaticModifier<Ciallo> CIALLO_STATIC_MODIFIER = MODIFIERS.register("ciallo", Ciallo::new);

    ///寂静 目前最没有设计感的材料
    public static final StaticModifier<SilenceAlmighty> SILENCE_ALMIGHTY_STATIC_MODIFIER = MODIFIERS.register("silence_almighty", SilenceAlmighty::new);




    ///ly
    public static final StaticModifier<Return> RETURN_STATIC_MODIFIER = MODIFIERS.register("return", Return::new);

    ///来自你们投稿
    public static final StaticModifier<DeepSeaGirl> DEEP_SEA_GIRL_STATIC_MODIFIER = MODIFIERS.register("deep_sea_girl", DeepSeaGirl::new);
    public static final StaticModifier<Reforge> REFORGE_STATIC_MODIFIER = MODIFIERS.register("reforge", Reforge::new);
    public static final StaticModifier<FireEater> FIRE_EATER_STATIC_MODIFIER = MODIFIERS.register("fire_eater", FireEater::new);
    public static final StaticModifier<Infested> INFESTED_STATIC_MODIFIER = MODIFIERS.register("infested", Infested::new);

    ///神圣
    public static final StaticModifier<HolyProtection> HOLY_PROTECTION_STATIC_MODIFIER = MODIFIERS.register("holy_protection", HolyProtection::new);



    ///彩虹物质
    public static final StaticModifier<ColorModifier> COLOR_MODIFIER_STATIC_MODIFIER = MODIFIERS.register("color", ColorModifier::new);
    public static final StaticModifier<NoLevelsModifier> COLOR1_MODIFIER_STATIC_MODIFIER = MODIFIERS.register("color1", NoLevelsModifier::new);

    ///领域
    public static final StaticModifier<DiademaModifier> ALEX_MELTDOWN_STATIC_MODIFIER = MODIFIERS.register("alex_meltdown", CommonDiademaModifier.Create(JzyyDiademaRegister.ALEX_MELTDOWN));
}
