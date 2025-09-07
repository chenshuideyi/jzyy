package com.csdy.jzyy.entity;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.entity.*;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = JzyyModMain.MODID)
public class JzyyEntityRegister {

    public static final DeferredRegister<EntityType<?>> JZYY_ENTITY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JzyyModMain.MODID);

//    public static final RegistryObject<EntityType<SwordManCsdy>> SWORD_MAN_CSDY = JZYY_ENTITY.

    public static final RegistryObject<EntityType<SwordManCsdy>> SWORD_MAN_CSDY =
            JZYY_ENTITY.register("sword_man_csdy",
                    () -> EntityType.Builder.of(SwordManCsdy::new, MobCategory.MONSTER)
                            .sized(1f, 1.95f) // 碰撞箱大小 (宽, 高)
                            .clientTrackingRange(14) // 客户端追踪距离
                            .build("jzyy:sword_man_csdy"));

    public static final RegistryObject<EntityType<Web13234>> WEB_13234 =
            JZYY_ENTITY.register("web_13234",
                    () -> EntityType.Builder.of(Web13234::new, MobCategory.MONSTER)
                            .sized(1f, 1.95f) // 碰撞箱大小 (宽, 高)
                            .clientTrackingRange(14) // 客户端追踪距离
                            .build("jzyy:web_13234"));

    public static final RegistryObject<EntityType<DogJiao>> DOG_JIAO =
            JZYY_ENTITY.register("dog_jiao",
                    () -> EntityType.Builder.of(DogJiao::new, MobCategory.MONSTER)
                            .sized(1f, 1.95f) // 碰撞箱大小 (宽, 高)
                            .clientTrackingRange(14) // 客户端追踪距离
                            .build("jzyy:dog_jiao"));

    public static final RegistryObject<EntityType<MiziAo>> MIZI_AO =
            JZYY_ENTITY.register("mizi_ao",
                    () -> EntityType.Builder.of(
                                    MiziAo::new, MobCategory.MONSTER)
                            .sized(1.2f, 2.55f)
                            .clientTrackingRange(14)
                            .build("jzyy:mizi_ao"));

    public static final RegistryObject<EntityType<DogJiaoJiaoJiao>> DOG_JIAO_JIAO_JIAO =
            JZYY_ENTITY.register("dog_jiao_jiao_jiao",
                    () -> EntityType.Builder.of(
                                    DogJiaoJiaoJiao::new, MobCategory.MONSTER)
                            .sized(10f, 10f)
                            .clientTrackingRange(14)
                            .build("jzyy:dog_jiao_jiao_jiao"));

    public static final RegistryObject<EntityType<HJMEntity>> HJM =
            JZYY_ENTITY.register("hjm",
                    () -> EntityType.Builder.of(HJMEntity::new, MobCategory.MONSTER)
                            .sized(0.65f, 0.65f) // 碰撞箱大小 (宽, 高)
                            .clientTrackingRange(33) // 客户端追踪距离
                            .build("jzyy:hjm"));

    public static final RegistryObject<EntityType<TitanWarden>> TITAN_WARDEN =
            JZYY_ENTITY.register("titan_warden",
                    () -> EntityType.Builder.of(
                                    TitanWarden::new, MobCategory.MONSTER)
                            //.sized(0.9F, 2.9F)
                            .sized(5.4F, 87F)
                            .clientTrackingRange(120)
                            .build("jzyy:titan_warden"));


    public JzyyEntityRegister(IEventBus modBus) {
        JZYY_ENTITY.register(modBus);
    }
}
