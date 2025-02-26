package com.csdy.frames;

import com.csdy.ModMain;
import com.csdy.frames.diadema.DiademaType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CsdyRegistries {
    public static final ResourceKey<Registry<DiademaType>> DIADEMA_TYPE = createRegistryKey("diadema_type");
    public static Supplier<IForgeRegistry<DiademaType>> DIADEMA_TYPES_REG;


    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent e) {
        RegistryBuilder<DiademaType> builder = new RegistryBuilder<>();
        builder.setName(DIADEMA_TYPE.location())
                .setDefaultKey(new ResourceLocation(ModMain.MODID, "default"));

        DIADEMA_TYPES_REG = e.create(builder);
    }

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation(ModMain.MODID, name));
    }
}
