package com.csdy.jzyy.entity;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.entity.boss.SwordManCsdy;
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


    public JzyyEntityRegister(IEventBus modBus) {
        JZYY_ENTITY.register(modBus);
    }
}
