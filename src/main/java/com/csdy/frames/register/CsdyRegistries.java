package com.csdy.frames.register;

import com.csdy.ModMain;
import com.csdy.frames.diadema.DiademaType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class CsdyRegistries {
    public static final ResourceKey<Registry<DiademaType>> DIADEMA_TYPE = createRegistryKey("diadema_type");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String p_259572_) {
        return ResourceKey.createRegistryKey(new ResourceLocation(ModMain.MODID, p_259572_));
    }
}
