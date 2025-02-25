package com.csdy.frames.diadema.register;

import com.csdy.ModMain;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.register.CsdyRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DiademaRegistry {

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent e) {
        RegistryBuilder<DiademaType> builder = new RegistryBuilder<>();
        builder.setName(CsdyRegistries.DIADEMA_TYPE.location())
                .setDefaultKey(new ResourceLocation(ModMain.MODID, "default"));

        e.create(builder);
    }
}
