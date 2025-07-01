package com.csdy.jzyy.item.tool.until;

import com.csdy.jzyy.CsdyTab;
import com.csdy.jzyy.item.tool.lollipop;
import com.csdy.jzyy.item.tool.tinker_loli_pickaxe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.csdy.jzyy.JzyyModMain.MODID;

public class JzyyTools extends CsdyTab{
    public JzyyTools() {
        SlotType.init();
        BlockSideHitListener.init();
        ModifierLootingHandler.init();
        RandomMaterial.init();
    }
    public static void initRegisters() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TINKER_ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
    }
    public static final ItemDeferredRegisterExtension TINKER_ITEMS = new ItemDeferredRegisterExtension(MODID);
    public static final SynchronizedDeferredRegister<CreativeModeTab> CREATIVE_TABS = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> tabTools = CREATIVE_TABS.register(
            "tools", () -> CreativeModeTab.builder()
                    .title(Component.translatable("jzyy.tool.tab"))
                    .icon(() -> JzyyTools.lollipop.get().getRenderTool())
                    .displayItems(JzyyTools::addTabItems)
                    .withTabsBefore(CsdyTab.TAB.getId())
                    .withSearchBar()
                    .build());

    private static void acceptTool(Consumer<ItemStack> output, Supplier<? extends IModifiable> tool) {
        ToolBuildHandler.addVariants(output, tool.get(), "");
    }

    public static final ItemObject<ModifiableItem> lollipop = TINKER_ITEMS.register("lollipop",()->new lollipop(new Item.Properties().stacksTo(1)));
    public static final ItemObject<ModifiableItem> tinker_loli_pickaxe = TINKER_ITEMS.register("tinker_loli_pickaxe",()->new tinker_loli_pickaxe(new Item.Properties().stacksTo(1)));

    private static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output tab) {
        Consumer<ItemStack> output = tab::accept;
        acceptTool(output, JzyyTools.lollipop);
        acceptTool(output, JzyyTools.tinker_loli_pickaxe);
    }
}
