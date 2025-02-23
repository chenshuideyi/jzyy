package com.csdy.item.register;

import com.csdy.ModMain;
import com.csdy.item.Death;
import com.csdy.item.food.Poop;
import com.csdy.item.material.BrokenSacredRelic;
import com.csdy.item.material.OriginalCatalyst;
import com.csdy.item.material.RebellionDebris;
import com.csdy.item.sword.*;
import com.csdy.item.sword.sword.Lapis;
import com.csdy.item.sword.sword.RedStone;
import com.csdy.item.util.ModTiers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
    public static final RegistryObject<Item> WEB_13234 = ITEMS.register("web_13234", () -> new Web_13234(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
    public static final RegistryObject<Item> REBELLION_DEBRIS = ITEMS.register("rebellion_debris", RebellionDebris::new);
    public static final RegistryObject<Item> BROKEN_SACRED_RELIC = ITEMS.register("broken_sacred_relic", BrokenSacredRelic::new);
    public static final RegistryObject<Item> ETSH = ITEMS.register("etsh_shadow_sword", () -> new Etsh(Tiers.DIAMOND, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> DOG = ITEMS.register("dog", () -> new Dog(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
    public static final RegistryObject<Item> REBELLION = ITEMS.register("rebellion", () -> new Rebellion(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
    public static final RegistryObject<Item> REDSTONE = ITEMS.register("redstone_sword", () -> new RedStone(ModTiers.REDSTONE, 2, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> LAPIS = ITEMS.register("lapis_sword", () -> new Lapis(ModTiers.LAPIS, 0, -1.4F, new Item.Properties()));
    public static final RegistryObject<Item> COAL = ITEMS.register("coal_sword", () -> new Lapis(ModTiers.COAL, 0, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> EMERALD = ITEMS.register("emerald_sword", () -> new Emerald(ModTiers.EMERALD, 0, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> PURPUR = ITEMS.register("purpur_sword", () -> new Emerald(ModTiers.PURPUR, 0, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> IRON_THEKNIFE = ITEMS.register("iron_theknife", () -> new Emerald(Tiers.IRON, 1, 0, new Item.Properties()));
    public static final RegistryObject<Item> SACRED_RELIC = ITEMS.register("sacred_relic", () -> new com.csdy.item.sword.SacredRelic(Tiers.NETHERITE, 0, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> DESOLATOR = ITEMS.register("desolator", () -> new com.csdy.item.hoe.Desolator(Tiers.NETHERITE, 0, -4F, new Item.Properties()));
    public static final RegistryObject<Item> DEMONEDGE = ITEMS.register("demon_edge", () -> new com.csdy.item.sword.DemonEdge(Tiers.NETHERITE, 0, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> NIGHT_OF_KNIGHTS = ITEMS.register("night_of_knights", () -> new com.csdy.item.sword.NightOfKnights(Tiers.NETHERITE, 0, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> COAL_SABER = ITEMS.register("coal_saber", () -> new com.csdy.item.sword.saber.CoalSaber(Tiers.DIAMOND, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> STONE_SABER = ITEMS.register("stone_saber", () -> new com.csdy.item.sword.saber.StoneSaber(Tiers.STONE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> IRON_SABER = ITEMS.register("iron_saber", () -> new com.csdy.item.sword.saber.IronSaber(Tiers.IRON, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_SABER = ITEMS.register("diamond_saber", () -> new com.csdy.item.sword.saber.DiamondSaber(Tiers.DIAMOND, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_SABER = ITEMS.register("netherite_saber", () -> new com.csdy.item.sword.saber.NetheriteSbaer(Tiers.NETHERITE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> BeloveSword = ITEMS.register("belove_sword", () -> new com.csdy.item.sword.BeloveSword(Tiers.NETHERITE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> POOP = ITEMS.register("poop", Poop::new);
    public static final RegistryObject<Item> ORIGINALCATALYST = ITEMS.register("original_catalyst", OriginalCatalyst::new);

}
