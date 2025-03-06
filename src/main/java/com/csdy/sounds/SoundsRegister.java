package com.csdy.sounds;

import com.csdy.ModMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;



public class SoundsRegister {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.MODID);

    // 注册声音事件
    public static final RegistryObject<SoundEvent> MELTDOWN = SOUND_EVENTS.register("meltdown", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("tcondiadema", "meltdown")));
    public static final RegistryObject<SoundEvent> LOLI_SUCCRSS = SOUND_EVENTS.register("loli_succrss", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("tcondiadema", "loli_succrss")));
    public static final RegistryObject<SoundEvent> LOLIRECORD = SOUND_EVENTS.register("lolirecord", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("tcondiadema", "lolirecord")));
}
