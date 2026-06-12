package com.csdy.jzyy.item.tool.until;

//import com.csdy.jzyy.item.tool.lollipop;
//import com.csdy.jzyy.item.tool.tinker_loli_pickaxe;
//
//public class JzyyTools extends CsdyTab{
//    public JzyyTools() {
//        SlotType.init();
//        BlockSideHitListener.init();
//        ModifierLootingHandler.init();
//        RandomMaterial.init();
//    }
//    public static void initRegisters() {
//        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
//        TINKER_ITEMS.register(bus);
//        CREATIVE_TABS.register(bus);
//    }
//    public static final ItemDeferredRegisterExtension TINKER_ITEMS = new ItemDeferredRegisterExtension(MODID);
//    public static final SynchronizedDeferredRegister<CreativeModeTab> CREATIVE_TABS = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
//
//    public static final RegistryObject<CreativeModeTab> tabTools = CREATIVE_TABS.register(
//            "tools", () -> CreativeModeTab.builder()
//                    .title(Component.translatable("jzyy.tool.tab"))
//                    .icon(() -> JzyyTools.lollipop.get().getRenderTool())
//                    .displayItems(JzyyTools::addTabItems)
//                    .withTabsBefore(CsdyTab.TAB.getId())
//                    .withSearchBar()
//                    .build());
//
//    private static void acceptTool(Consumer<ItemStack> output, Supplier<? extends IModifiable> tool) {
//        ToolBuildHandler.addVariants(output, tool.get(), "");
//    }
//
//    public static final ItemObject<ModifiableItem> lollipop = TINKER_ITEMS.register("lollipop",()->new lollipop(new Item.Properties().stacksTo(1)));
//    public static final ItemObject<ModifiableItem> tinker_loli_pickaxe = TINKER_ITEMS.register("tinker_loli_pickaxe",()->new tinker_loli_pickaxe(new Item.Properties().stacksTo(1)));
//
//    private static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output tab) {
//        Consumer<ItemStack> output = tab::accept;
//        acceptTool(output, JzyyTools.lollipop);
//        acceptTool(output, JzyyTools.tinker_loli_pickaxe);
//    }
//}
