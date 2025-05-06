package com.csdy.jzyy;

import com.csdy.jzyy.diadema.JzyyClientDiademaRegister;
import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.effect.JzyyEffectRegister;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.boss.SwordManCsdy;
import com.csdy.jzyy.entity.boss.render.JzyyEntityRenderer;
import com.csdy.jzyy.item.register.HideRegister;
import com.csdy.jzyy.item.register.ItemRegister;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import com.csdy.jzyy.modifier.util.layer.BloodLayer;
import com.csdy.jzyy.modifier.util.layer.GocLayer;
import com.csdy.jzyy.modifier.util.layer.ScpLayer;
import com.csdy.jzyy.modifier.util.layer.PlayerLayer;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
//TODO 潘多拉，提亚马特和其他东西的注册名
public class ModMain {

    public static int toolTip = 1;
    public static final String MODID = "jzyy";
    private static final Minecraft mc = Minecraft.getInstance();

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


        JzyyDiademaRegister.DIADEMA_TYPES.register(bus);
        //仅客户端运行
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            JzyyClientDiademaRegister.CLIENT_DIADEMA_TYPES.register(bus);
        });

        (new Thread(() -> {
            while(mc.isRunning()) {
                synchronized(Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(1000L);
                    } catch (Exception var3) {
                    }
                }

                ++toolTip;
                if (toolTip > 4) {
                    toolTip = 1;
                }
            }

        })).start();
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        //网络包
        JzyySyncing.Init();
        // 以下代码仅在客户端运行
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DxSlots::init);

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
    public static void onRegisterLayers(EntityRenderersEvent.AddLayers event) {
        // 为所有玩家皮肤类型注册所有可能的渲染层
        addLayersToRenderer(event, "default");
        addLayersToRenderer(event, "slim");
    }

    private static void addLayersToRenderer(EntityRenderersEvent.AddLayers event, String skinType) {
        PlayerRenderer renderer = event.getSkin(skinType);
        if (renderer != null) {
            // 一次性添加所有可能的层
            renderer.addLayer(new PlayerLayer(renderer));
            renderer.addLayer(new BloodLayer(renderer));
            renderer.addLayer(new ScpLayer(renderer));
            renderer.addLayer(new GocLayer(renderer));
        }
    }
}

//    @SubscribeEvent
//    @OnlyIn(Dist.CLIENT)
//    public void onRenderPlayer(RenderPlayerEvent.Pre e) {
//        // 凋零护甲
//        if (e.getEntity().hasEffect(JzyyEffectRegister.OVERCHARGE_ARMOR.get())) {
//            e.getRenderer().addLayer(new PlayerLayer(e.getRenderer()));
//        }
//        // 血怒效果
//        if (e.getEntity().hasEffect(JzyyEffectRegister.OVERCHARGE.get())) {
//            e.getRenderer().addLayer(new BloodLayer(e.getRenderer()));
//        }
//        if (e.getEntity() != null){
//            e.getRenderer().addLayer(new HaloRenderLayer(e.getRenderer()));
//        }
//}
//}

