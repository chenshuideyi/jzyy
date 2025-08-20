package com.csdy.jzyy.item.register;

import com.csdy.jzyy.block.BlockRegister;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.item.*;
import com.csdy.jzyy.item.food.FriedChicken;
import com.csdy.jzyy.item.food.GoldenStrawberry;
import com.csdy.jzyy.item.food.HotDog;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
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
    public static final RegistryObject<Item> DX_INGOT = ITEMS.register("dx_ingot", DxIngot::new);
    public static final RegistryObject<Item> ICE = ITEMS.register("ice", Ice::new);
    public static final RegistryObject<Item> TONG_BAN = ITEMS.register("tong_ban", TongBan::new);
    public static final RegistryObject<Item> RAIN_CURTAIN = ITEMS.register("rain_curtain", RainCurtain::new);
    public static final RegistryObject<Item> YAKUMO_ENDER = ITEMS.register("yakumo_ender", YakumoEnder::new);
    public static final RegistryObject<Item> FAILED_GOBBERS = ITEMS.register("failed_gobbers", FaliedGobbers::new);

    public static final RegistryObject<Item> GOLDEN_STRAWBERRY = ITEMS.register("golden_strawberry", GoldenStrawberry::new);
    public static final RegistryObject<Item> FRIED_CHICKEN = ITEMS.register("fried_chicken", FriedChicken::new);

    public static final RegistryObject<Item> RAINBOW_MATERIAL = ITEMS.register("rainbow_material", RainbowMaterial::new);
    public static final RegistryObject<Item> SILENCE = ITEMS.register("silence", Silence::new);
    public static final RegistryObject<Item> MAGGOT = ITEMS.register("maggot", Maggot::new);
    public static final RegistryObject<Item> HOT_DOG = ITEMS.register("hot_dog", HotDog::new);


    public static final RegistryObject<BaseSponsorshipItem> STARMETAL_INGOT = ITEMS.register("evange_alloy_ingot",
            () -> new BaseSponsorshipItem(Rarity.RARE,"item.jzyy.starmetal_ingot.line1", ChatFormatting.ITALIC));

    public static final RegistryObject<BaseSponsorshipItem> EVANGE_ALLOY_INGOT = ITEMS.register("evange_alloy_ingot",
            () -> new BaseSponsorshipItem(Rarity.RARE,"item.jzyy.evange_alloy_ingot.line1", ChatFormatting.AQUA));

    public static final RegistryObject<BaseSponsorshipItem> RETURN_TO_THE_END = ITEMS.register("return_to_the_end",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.return_to_the_end.line1", ChatFormatting.DARK_PURPLE));

    public static final RegistryObject<BaseSponsorshipItem> YING = ITEMS.register("ying",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.ying.line1", ChatFormatting.BLACK));

    public static final RegistryObject<BaseSponsorshipItem> YANG = ITEMS.register("yang",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.yang.line1", ChatFormatting.WHITE));

    public static final RegistryObject<Item> DOG_JIAO_SPAWN_EGG = ITEMS.register("dog_jiao_spawn_egg", () -> new ForgeSpawnEggItem(JzyyEntityRegister.DOG_JIAO,  -10998784, -8650752, new Item.Properties()));
    public static final RegistryObject<Item> HJM_SPAWN_EGG = ITEMS.register("hjm_spawn_egg", () -> new ForgeSpawnEggItem(JzyyEntityRegister.HJM,  -4682658, -2311533, new Item.Properties()));
    public static final RegistryObject<Item> TITAN_WARDEN_SPAWN_EGG = ITEMS.register("titan_warden_spawn_egg", () -> new ForgeSpawnEggItem(JzyyEntityRegister.TITAN_WARDEN,  993082, 330000, new Item.Properties()));

    public static final RegistryObject<Item> HARCADIUM = ITEMS.register(
            "harcadium",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.RARE)
            )
    );

    public static final RegistryObject<Item> HARCADIUM_ORE = ITEMS.register(
            "harcadium_ore",
            () -> new BlockItem(
                    BlockRegister.HARCADIUM_ORE.get(),
                    new Item.Properties()
            )
    );

    public static final RegistryObject<Item> HARCADIUM_ORE_END_STONE = ITEMS.register(
            "harcadium_ore_end_stone",
            () -> new BlockItem(
                    BlockRegister.HARCADIUM_ORE_END_STONE.get(),
                    new Item.Properties()
            )
    );

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
    public static final RegistryObject<Item> HALLOWED_BAR = ITEMS.register("hallowed_bar",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));


    public static final RegistryObject<Item> PORTABLE_END_PORTAL = ITEMS.register("portable_end_portal", PortableEndPortal::new);

    public static final RegistryObject<Item> MOZHUA_CAP = ITEMS.register("mozhua_cap",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> SUMMON_CSDY = ITEMS.register("summon_csdy", SummonCsdy::new);


}
