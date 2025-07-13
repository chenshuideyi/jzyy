package com.csdy.jzyy.item.register;

import com.csdy.jzyy.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.csdy.jzyy.JzyyModMain.MODID;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> MARISA = ITEMS.register("marisa_ingot", MarisaIngot::new);
    public static final RegistryObject<Item> YUE_ZHENG_LING = ITEMS.register("yue_zheng_ling", YueZhengLing::new);
    public static final RegistryObject<Item> ABYSS_ALLOY = ITEMS.register("abyss_alloy", AbyssAlloy::new);
    public static final RegistryObject<Item> MAGIC_URANIUM = ITEMS.register("magic_uranium", MagicUranium::new);
    public static final RegistryObject<Item> OVERFLOWED_MEMORY = ITEMS.register("overflowed_memory", OverflowedMemory::new);
    public static final RegistryObject<Item> ETSH = ITEMS.register("etsh", Etsh::new);
//    public static final RegistryObject<Item> MTF = ITEMS.register("mtf", Mtf::new);
    public static final RegistryObject<Item> DX_INGOT = ITEMS.register("dx_ingot", DxIngot::new);
    public static final RegistryObject<Item> ICE = ITEMS.register("ice", Ice::new);
    public static final RegistryObject<Item> TONG_BAN = ITEMS.register("tong_ban", TongBan::new);
    public static final RegistryObject<Item> RAIN_CURTAIN = ITEMS.register("rain_curtain", RainCurtain::new);
    public static final RegistryObject<Item> YAKUMO_ENDER = ITEMS.register("yakumo_ender", YakumoEnder::new);


    public static final RegistryObject<Item> HARCADIUM = ITEMS.register("harcadium",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> EXPERIENCE_STEEL_INGOT = ITEMS.register("experience_steel_ingot",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> LIVING_WOOD = ITEMS.register("living_wood",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> UNPOWERED_REDSTONE_COMPONENT = ITEMS.register("unpowered_redstone_component",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> PLA_STEEL_INGOT = ITEMS.register("pla_steel_ingot",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> DEFECT = ITEMS.register("defect",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));

//    public static final RegistryObject<DiscAloneInTheDarkItem> TEST_DISC = ITEMS.register("test_disc", DiscAloneInTheDarkItem::new);

}
