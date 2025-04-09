package com.csdy.jzyy.sounds;


import com.csdy.tcondiadema.ModMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class JzyySoundsRegister {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.MODID);

    // 注册声音事件
    public static final RegistryObject<SoundEvent> IMAGINE_BREAKER = SOUND_EVENTS.register("imagine_breaker", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jzyy", "imagine_breaker")));
}
