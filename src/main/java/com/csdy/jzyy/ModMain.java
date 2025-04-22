package com.csdy.jzyy;

import com.csdy.jzyy.effect.JzyyEffectRegister;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.boss.SwordManCsdy;
import com.csdy.jzyy.entity.boss.render.JzyyEntityRenderer;
import com.csdy.jzyy.item.register.HideRegister;
import com.csdy.jzyy.item.register.ItemRegister;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import com.csdy.jzyy.modifier.util.PlayerLayer;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.DiademaSlots;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.csdy.jzyy.modifier.modifier.etsh.GpuUtil.gpuUtilInit;

@Mod(ModMain.MODID)
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//TODO 耻辱下播 启示录FE材料联动
public class ModMain {

    public static final String MODID = "jzyy";

    public ModMain() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CsdyTab.CREATIVE_MODE_TABS.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
        ModifierRegister.MODIFIERS.register(bus);
        ItemRegister.ITEMS.register(bus);
        HideRegister.ITEMS.register(bus);
        JzyySoundsRegister.SOUND_EVENTS.register(bus);
        JzyyEffectRegister.EFFECTS.register(bus);
        JzyyEntityRegister.JZYY_ENTITY.register(bus);
        gpuUtilInit();
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        //网络包
        JzyySyncing.Init();
//        // 以下代码仅在客户端运行
//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//            registerRenderers();
//        });

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void addAttribute(EntityAttributeCreationEvent event) {
        event.put(JzyyEntityRegister.SWORD_MAN_CSDY.get(), SwordManCsdy.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // 注册你的GeoEntity渲染器
        event.registerEntityRenderer(
                JzyyEntityRegister.SWORD_MAN_CSDY.get(),
                JzyyEntityRenderer::new // 渲染器构造函数引用
        );
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderPlayer(RenderPlayerEvent.Pre e) {
        //凋零护甲
        if (!e.getEntity().getPersistentData().getBoolean("isPlayer") && e.getEntity().hasEffect(JzyyEffectRegister.OVERCHARGE_ARMOR.get())) {
            e.getRenderer().addLayer(new PlayerLayer(e.getRenderer()));
            e.getEntity().getPersistentData().putBoolean("isPlayer", true);
        }
    }

}

