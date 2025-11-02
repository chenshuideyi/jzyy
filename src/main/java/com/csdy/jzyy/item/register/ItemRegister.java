package com.csdy.jzyy.item.register;

import com.csdy.jzyy.block.BlockRegister;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.font.Rarity.ExtendedRarity;
import com.csdy.jzyy.item.*;
import com.csdy.jzyy.item.food.FriedChicken;
import com.csdy.jzyy.item.food.GoldenStrawberry;
import com.csdy.jzyy.item.food.HotDog;
import com.csdy.jzyy.item.kill_count.BaseKillCountCounter;
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

    public static final RegistryObject<Item> MINI_CSDY = ITEMS.register("mini_csdy", MiniCsdy::new);
    public static final RegistryObject<Item> DOT = ITEMS.register("dot_item", DotItem::new);
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
    public static final RegistryObject<Item> TIANYUAN_BOX = ITEMS.register("tianyuan_box", TianyuanBox::new);

    public static final RegistryObject<Item> GOLDEN_STRAWBERRY = ITEMS.register("golden_strawberry", GoldenStrawberry::new);
    public static final RegistryObject<Item> FRIED_CHICKEN = ITEMS.register("fried_chicken", FriedChicken::new);

    public static final RegistryObject<Item> RAINBOW_MATERIAL = ITEMS.register("rainbow_material", RainbowMaterial::new);
    public static final RegistryObject<Item> SILENCE = ITEMS.register("silence", Silence::new);
    public static final RegistryObject<Item> MAGGOT = ITEMS.register("maggot", Maggot::new);
    public static final RegistryObject<Item> HOT_DOG = ITEMS.register("hot_dog", HotDog::new);



    public static final RegistryObject<BaseSponsorshipItem> STARMETAL_INGOT = ITEMS.register("starmetal_ingot",
            () -> new BaseSponsorshipItem(Rarity.RARE,"item.jzyy.starmetal_ingot.line1", ChatFormatting.ITALIC));

    public static final RegistryObject<BaseSponsorshipItem> EVANGE_ALLOY_INGOT = ITEMS.register("evange_alloy_ingot",
            () -> new BaseSponsorshipItem(Rarity.RARE,"item.jzyy.evange_alloy_ingot.line1", ChatFormatting.AQUA));

    public static final RegistryObject<BaseSponsorshipItem> RETURN_TO_THE_END = ITEMS.register("return_to_the_end",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.return_to_the_end.line1", ChatFormatting.DARK_PURPLE));

    public static final RegistryObject<BaseSponsorshipItem> YIN = ITEMS.register("yin",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.yin.line1", ChatFormatting.BLACK));

    public static final RegistryObject<BaseSponsorshipItem> YANG = ITEMS.register("yang",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.yang.line1", ChatFormatting.WHITE));

    public static final RegistryObject<BaseSponsorshipItem> ARDITE_INGOT = ITEMS.register("ardite_ingot",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.ardite_ingot.line1", ChatFormatting.WHITE));

    public static final RegistryObject<BaseSponsorshipItem> TIAN_YI_INGOT = ITEMS.register("tian_yi_ingot",
            () -> new BaseSponsorshipItem(Rarity.EPIC,"item.jzyy.tian_yi_ingot.line1", ChatFormatting.BLUE));

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
    public static final RegistryObject<Item> MEGA_MANYULLYN = ITEMS.register("mega_manyullyn",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> MEGA_MANYULLYN_BOOSTER_ENERGY = ITEMS.register("mega_manyullyn_booster_energy",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC)));



    public static final RegistryObject<Item> PORTABLE_END_PORTAL = ITEMS.register("portable_end_portal", PortableEndPortal::new);

    public static final RegistryObject<Item> MOZHUA_CAP = ITEMS.register("mozhua_cap",
            () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> SUMMON_CSDY = ITEMS.register("summon_csdy", SummonCsdy::new);



    public static final RegistryObject<Item> KILL_COUNT_COUNTER_6 = ITEMS.register("kill_count_counter_6",() -> new BaseKillCountCounter(Rarity.COMMON,6));
    public static final RegistryObject<Item> KILL_COUNT_COUNTER_30 = ITEMS.register("kill_count_counter_30",() -> new BaseKillCountCounter(Rarity.UNCOMMON,30));
    public static final RegistryObject<Item> KILL_COUNT_COUNTER_68 = ITEMS.register("kill_count_counter_60",() -> new BaseKillCountCounter(Rarity.RARE,68));
    public static final RegistryObject<Item> KILL_COUNT_COUNTER_128 = ITEMS.register("kill_count_counter_128",() -> new BaseKillCountCounter(Rarity.EPIC,128));
    public static final RegistryObject<Item> KILL_COUNT_COUNTER_328 = ITEMS.register("kill_count_counter_328",() -> new BaseKillCountCounter(ExtendedRarity.LEGENDARY,328));
    public static final RegistryObject<Item> KILL_COUNT_COUNTER_648 = ITEMS.register("kill_count_counter_648",() -> new BaseKillCountCounter(ExtendedRarity.RAINBOW,648));

}
