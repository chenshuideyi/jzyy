package com.csdy.jzyy;

import com.csdy.jzyy.block.BlockRegister;
import com.csdy.jzyy.diadema.JzyyClientDiademaRegister;
import com.csdy.jzyy.diadema.JzyyDiademaRegister;
import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.boss.entity.*;

import com.csdy.jzyy.entity.boss.render.*;
import com.csdy.jzyy.entity.monster.entity.DogJiao;
import com.csdy.jzyy.entity.monster.entity.HJMEntity;
import com.csdy.jzyy.entity.monster.render.DogJiaoRenderer;
import com.csdy.jzyy.entity.monster.render.HJMRenderer;
import com.csdy.jzyy.event.LivingEvent;
import com.csdy.jzyy.fluid.register.JzyyFluidRegister;
import com.csdy.jzyy.item.register.HideRegister;
import com.csdy.jzyy.item.register.ItemRegister;
import com.csdy.jzyy.item.tool.until.JzyyTools;
import com.csdy.jzyy.shader.BatBlindnessEffect;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import com.csdy.jzyy.modifier.util.JzyyAnimationHandler;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.particle.register.JzyyParticlesRegister;
import com.csdy.jzyy.shader.BloodSkyEffect;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;

import static com.csdy.jzyy.coremod.CsdyLaunchPluginService.checkJavaVersion;
import static com.csdy.jzyy.coremod.CsdyLaunchPluginService.checkOculus;
import static com.csdy.jzyy.item.tool.until.JzyyTools.lollipop;
import static com.csdy.jzyy.item.tool.until.JzyyTools.tinker_loli_pickaxe;
import static com.csdy.jzyy.modifier.modifier.etsh.GpuUtil.gpuUtilInit;

@Mod(JzyyModMain.MODID)
@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//TODO 写阴阳材料，步骤如下：
//1 创造两个材料“阳”和“阳”，阴熔铸成流体阳，反之亦然
//2 写一个词条Ying 检测到工具不含材料阳和阴 但是拥有特性阳的时候去除自身和阳的并添加其他三个特性
// 两个一起刻印上去生效,亘古永恒，无视地形飞行，伤害减免，抗性5夜视饱和生命回复，生命上限翻倍
public class JzyyModMain {

    public static int toolTip = 1;
    public static final String MODID = "jzyy";
    private static final Minecraft mc = Minecraft.getInstance();

    public JzyyModMain() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, JzyyConfig.JZYY_CONFIG);

        CsdyTab.CREATIVE_MODE_TABS.register(bus);
        JzyyTools.initRegisters();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new LivingEvent());

        ModifierRegister.MODIFIERS.register(bus);
        ItemRegister.ITEMS.register(bus);
        HideRegister.ITEMS.register(bus);
        BlockRegister.BLOCKS.register(bus);
        JzyySoundsRegister.SOUND_EVENTS.register(bus);
        JzyyEffectRegister.EFFECTS.register(bus);
        JzyyEntityRegister.JZYY_ENTITY.register(bus);
        JzyyFluidRegister.FLUIDS.register(bus);
        JzyyParticlesRegister.PARTICLE_TYPES.register(bus);
        MinecraftForge.EVENT_BUS.register(JzyyAnimationHandler.class);




        checkJavaVersion();
        checkOculus();

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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> event.enqueueWork(BatBlindnessEffect::init));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> event.enqueueWork(BloodSkyEffect::init));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void addAttribute(EntityAttributeCreationEvent event) {
        event.put(JzyyEntityRegister.SWORD_MAN_CSDY.get(), SwordManCsdy.createAttributes().build());
        event.put(JzyyEntityRegister.DOG_JIAO.get(), DogJiao.createAttributes().build());
        event.put(JzyyEntityRegister.DOG_JIAO_JIAO_JIAO.get(), DogJiaoJiaoJiao.createAttributes().build());
        event.put(JzyyEntityRegister.MIZI_AO.get(), MiziAo.createAttributes().build());
        event.put(JzyyEntityRegister.HJM.get(), HJMEntity.createAttributes().build());
        event.put(JzyyEntityRegister.TITAN_WARDEN.get(), TitanWarden.createAttributes().build());
        event.put(JzyyEntityRegister.WEB_13234.get(), Web13234.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // 注册你的GeoEntity渲染器
        event.registerEntityRenderer(
                JzyyEntityRegister.SWORD_MAN_CSDY.get(),
                SwordManCsdyRenderer::new // 渲染器构造函数引用
        );
        event.registerEntityRenderer(
                JzyyEntityRegister.DOG_JIAO.get(),
                DogJiaoRenderer::new
        );
        event.registerEntityRenderer(
                JzyyEntityRegister.MIZI_AO.get(),
                MiziAoRenderer::new
        );
        event.registerEntityRenderer(
                JzyyEntityRegister.DOG_JIAO_JIAO_JIAO.get(),
                DogJiaoJiaoJiaoRenderer::new
        );
        event.registerEntityRenderer(
                JzyyEntityRegister.HJM.get(),
                HJMRenderer::new
        );
        event.registerEntityRenderer(
                JzyyEntityRegister.TITAN_WARDEN.get(),
                TitanWardenRenderer::new
        );
        event.registerEntityRenderer(
                JzyyEntityRegister.WEB_13234.get(),
                Web13234Renderer::new
        );
    }

    public static ResourceLocation getResourceLoc(String id) {
        return new ResourceLocation(MODID,id);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                TinkerItemProperties.registerToolProperties(lollipop.get());
                TinkerItemProperties.registerBrokenProperty(lollipop.get());

                TinkerItemProperties.registerToolProperties(tinker_loli_pickaxe.get());
                TinkerItemProperties.registerBrokenProperty(tinker_loli_pickaxe.get());
            });
        }
    }




    static {
        System.setProperty("java.awt.headless", "false");
    }
}

