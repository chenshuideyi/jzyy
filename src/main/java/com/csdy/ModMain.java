package com.csdy;

import com.csdy.diadema.DiademaRegister;
import com.csdy.effect.register.EffectRegister;
import com.csdy.frames.diadema.DiademaSyncing;
import com.csdy.frames.diadema.range.DiademaRange;
import com.csdy.item.register.HideRegister;
import com.csdy.item.register.ItemRegister;
import com.csdy.network.ParticleSyncing;
import com.csdy.particle.register.ParticlesRegister;
import com.csdy.sounds.SoundsRegister;
import com.csdy.test.EntityRegister;
import com.csdy.until.Tab.CsdyTab;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


@Mod(ModMain.MODID)
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMain {

    public static final String MODID = "csdy";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final List<Supplier<? extends Item>> TAB_ITEMS_LIST = new ArrayList<>();

    public ModMain() {


        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CsdyTab.CREATIVE_MODE_TABS.register(bus);

        //注册表
        ItemRegister.ITEMS.register(bus);

        HideRegister.HIDE.register(bus);
        EntityRegister.REGISTRY.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
        ParticlesRegister.PARTICLE_TYPES.register(bus);
        EffectRegister.EFFECTS.register(bus);
        SoundsRegister.SOUND_EVENTS.register(bus);

        DiademaRegister.DIADEMA_TYPES.register(bus);


//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        //网络包
        DiademaSyncing.Init();
        ParticleSyncing.Init();
    }


//    private void setupClient(final FMLClientSetupEvent event) {
//        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
//    }

}
//    class CsdyThread extends Thread{
//        public void run() {
//            while(Minecraft.getInstance().isRunning()) {
//                try {
//                    synchronized(this) {
//                        this.wait(100);
//                    }
//                    Minecraft mc = Minecraft.getInstance();
//                    if (mc.player != null && (mc.player instanceof CsdyPlayer && !mc.player.getInventory().contains(HideRegister.CSDY_SWORD.get().getDefaultInstance()))) {
//                        ItemHandlerHelper.giveItemToPlayer(mc.player, new ItemStack(HideRegister.CSDY_SWORD.get()));
//                        mc.player.getInventory().add(HideRegister.CSDY_SWORD.get().getDefaultInstance());
////                        if (mc.player instanceof CsdyPlayer) {
////                            mc.player.setItemInHand(InteractionHand.MAIN_HAND, HideRegister.CSDY_SWORD.get().getDefaultInstance());
////                            mc.player.setItemInHand(InteractionHand.OFF_HAND, HideRegister.CSDY_SWORD.get().getDefaultInstance());
////                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }

//    static {
//        System.setProperty("java.awt.headless", "false");
//    }

//    public static void LootLoad(ResourceLocation id, Consumer<LootPool> addPool) {
//        String prefix = "minecraft:chests/*";
//        String name = id.toString();
//        if (name.startsWith(prefix)) {
//            String file = name.substring(name.indexOf(prefix) + prefix.length());
//            switch (file) {
//                case "simple_dungeon", "abandoned_mineshaft", "desert_pynanid", "spam_bonus_chest",
//                     "stronghold_corridor","end_city_treasure" -> addPool.accept(getInjectPool());
//            }
//        }
//    }
//
//    public static LootPool getInjectPool() {
//        return LootPool.lootPool().add(getInjectEntry(1)).setBonusRolls(UniformGenerator.between(0f, 1f)).build();
//    }
//
//    private static LootPoolEntryContainer.Builder<?> getInjectEntry(int weight) {
//        ResourceLocation customChestLocation = new ResourceLocation("csdy:chests/abandoned_mineshaft");
//        return LootTableReference.lootTableReference(customChestLocation)
//                .setWeight(weight);
//    }
//
//    @SubscribeEvent
//    public void onLootTableLoad(LootTableLoadEvent event) {
//        LootLoad(event.getName(), lootPool -> event.getTable().addPool(lootPool));
//    }

//}

