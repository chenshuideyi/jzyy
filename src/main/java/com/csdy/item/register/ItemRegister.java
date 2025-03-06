package com.csdy.item.register;

import com.csdy.ModMain;
import com.csdy.item.food.EnderDragonHeart;
import com.csdy.item.food.WardenHeart;
import com.csdy.item.food.Poop;
import com.csdy.item.material.*;
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
//    public static final RegistryObject<Item> WEB_13234 = ITEMS.register("web_13234", () -> new Web_13234(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
//    public static final RegistryObject<Item> REBELLION_DEBRIS = ITEMS.register("rebellion_debris", RebellionDebris::new);
    public static final RegistryObject<Item> BROKEN_SACRED_RELIC = ITEMS.register("broken_sacred_relic", BrokenSacredRelic::new);
//    public static final RegistryObject<Item> ETSH = ITEMS.register("etsh_shadow_sword", () -> new Etsh(Tiers.DIAMOND, 3, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> DOG = ITEMS.register("dog", () -> new Dog(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
//    public static final RegistryObject<Item> REBELLION = ITEMS.register("rebellion", () -> new Rebellion(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
//    public static final RegistryObject<Item> REDSTONE = ITEMS.register("redstone_sword", () -> new RedStone(ModTiers.REDSTONE, 2, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> LAPIS = ITEMS.register("lapis_sword", () -> new Lapis(ModTiers.LAPIS, 0, -1.4F, new Item.Properties()));
//    public static final RegistryObject<Item> COAL = ITEMS.register("coal_sword", () -> new Lapis(ModTiers.COAL, 0, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> EMERALD = ITEMS.register("emerald_sword", () -> new Emerald(ModTiers.EMERALD, 0, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> PURPUR = ITEMS.register("purpur_sword", () -> new Emerald(ModTiers.PURPUR, 0, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> IRON_THEKNIFE = ITEMS.register("iron_theknife", () -> new Emerald(Tiers.IRON, 1, 0, new Item.Properties()));
    public static final RegistryObject<Item> SACRED_RELIC = ITEMS.register("sacred_relic", () -> new com.csdy.item.sword.SacredRelic(Tiers.NETHERITE, 0, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> BeloveSword = ITEMS.register("belove_sword", () -> new com.csdy.item.sword.BeloveSword(Tiers.NETHERITE, 3, -2.4F, new Item.Properties()));
//    public static final RegistryObject<Item> POOP = ITEMS.register("poop", Poop::new);
//    public static final RegistryObject<Item> ORIGINALCATALYST = ITEMS.register("original_catalyst", OriginalCatalyst::new);
    public static final RegistryObject<Item> WARDEN_HEART = ITEMS.register("warden_heart", WardenHeart::new);
//    public static final RegistryObject<Item> THE_SILVER_KEY = ITEMS.register("the_silver_key", TheSilverKey::new);
    public static final RegistryObject<Item> ENDERDRAGON_HEART = ITEMS.register("enderdragon_heart", EnderDragonHeart::new);
    public static final RegistryObject<Item> FORMA = ITEMS.register("forma", Forma::new);
    public static final RegistryObject<Item> NEURON = ITEMS.register("neuron", Neuron::new);
    public static final RegistryObject<Item> MORPHICS = ITEMS.register("morphics", Morphics::new);
    public static final RegistryObject<Item> OROKIN_CELL = ITEMS.register("orokin_cell", OrokinCell::new);
    public static final RegistryObject<Item> NEURAL_SENSORS = ITEMS.register("neural_sensors", NeuralSensors::new);
    public static final RegistryObject<Item> AURA_FORMA = ITEMS.register("aura_forma", AuraForma::new);



}
