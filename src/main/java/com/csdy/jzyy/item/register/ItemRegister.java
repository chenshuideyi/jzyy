package com.csdy.jzyy.item.register;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.item.*;
import com.csdy.jzyy.item.disc.DiscAloneInTheDarkItem;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
    public static final RegistryObject<Item> MARISA = ITEMS.register("marisa_ingot", MarisaIngot::new);
    public static final RegistryObject<Item> YUE_ZHENG_LING = ITEMS.register("yue_zheng_ling", YueZhengLing::new);
    public static final RegistryObject<Item> ABYSS_ALLOY = ITEMS.register("abyss_alloy", AbyssAlloy::new);
    public static final RegistryObject<Item> MAGIC_URANIUM = ITEMS.register("magic_uranium", MagicUranium::new);
    public static final RegistryObject<Item> OVERFLOWED_MEMORY = ITEMS.register("overflowed_memory", OverflowedMemory::new);

//    public static final RegistryObject<DiscAloneInTheDarkItem> TEST_DISC = ITEMS.register("test_disc", DiscAloneInTheDarkItem::new);
}
