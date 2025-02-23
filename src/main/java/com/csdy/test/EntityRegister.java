package com.csdy.test;


import com.csdy.ModMain;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> REGISTRY;
    public static final RegistryObject<EntityType<Entity>> RAINBOW_LIGHTING;
    public static final RegistryObject<EntityType<Entity>> RAINBOW_LIGHTING2;


    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> entityTypeBuilder.build(registryname));
    }

    static {
        REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModMain.MODID);
        RAINBOW_LIGHTING = register("rainbow_lighting", Builder.of(ColorLightningBolt::new,
                MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).
                setUpdateInterval(3).setCustomClientFactory(ColorLightningBolt::new).sized(0.6F, 1.8F));

        RAINBOW_LIGHTING2 = register("rainbow_lighting2", Builder.of(ColorLightningBolt2::new,
                        MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).
                setUpdateInterval(3).setCustomClientFactory(ColorLightningBolt2::new).sized(0.6F, 1.8F));
    }
}
