package com.csdy.jzyy.sounds;


import com.csdy.jzyy.JzyyModMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class JzyySoundsRegister {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JzyyModMain.MODID);

    // 注册声音事件
    public static final RegistryObject<SoundEvent> IMAGINE_BREAKER = SOUND_EVENTS.register("imagine_breaker",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "imagine_breaker")));

    public static final RegistryObject<SoundEvent> CUMULONIMBUS = SOUND_EVENTS.register("cumulonimbus",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "cumulonimbus")));



    public static final RegistryObject<SoundEvent> AMA_NO_JYAKU = SOUND_EVENTS.register("ama_no_jyaku",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "ama_no_jyaku")));

    public static final RegistryObject<SoundEvent> MONSTER_SONG = SOUND_EVENTS.register("monster_song",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "monster_song")));

    public static final RegistryObject<SoundEvent> SIX_TRILLION_YEARS = SOUND_EVENTS.register("six_trillion_years",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "six_trillion_years")));

    public static final RegistryObject<SoundEvent> THERMAL_ANOMALY = SOUND_EVENTS.register("thermal_anomaly",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "thermal_anomaly")));

    public static final RegistryObject<SoundEvent> I_LOVE_U = SOUND_EVENTS.register("i_love_u",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "i_love_u")));

    public static final RegistryObject<SoundEvent> THE_MILLENNIUM_SNOW = SOUND_EVENTS.register("the_millennium_snow",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "the_millennium_snow")));

    public static final RegistryObject<SoundEvent> ONLY_WISH = SOUND_EVENTS.register("only_wish",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "only_wish")));

    public static final RegistryObject<SoundEvent> PANDORA = SOUND_EVENTS.register("pandora",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "pandora")));

    public static final RegistryObject<SoundEvent> TIAMAT = SOUND_EVENTS.register("tiamat",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "tiamat")));

    public static final RegistryObject<SoundEvent> GIRL_A = SOUND_EVENTS.register("girl_a",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "girl_a")));

    public static final RegistryObject<SoundEvent> UMIYURI_KAITEITAN = SOUND_EVENTS.register("umiyuri_kaiteitan",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "umiyuri_kaiteitan")));

    public static final RegistryObject<SoundEvent> DOG_JIAO_SUMMON = SOUND_EVENTS.register("dog_jiao_summon",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "dog_jiao_summon")));

    public static final RegistryObject<SoundEvent> CIALLO = SOUND_EVENTS.register("ciallo",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "ciallo")));

}
