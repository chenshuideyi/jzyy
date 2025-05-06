package com.csdy.jzyy.item.register;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.item.fake.FakeItem;
import com.csdy.jzyy.item.Test;
import com.csdy.jzyy.item.food.CsdySword;
import com.csdy.jzyy.item.team.StarLight;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HideRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
    public static final RegistryObject<Item> TEST = ITEMS.register("test", Test::new);
    public static final RegistryObject<Item> FAKE_ITEM = ITEMS.register("null", FakeItem::new);
    public static final RegistryObject<Item> STAR_LIGHT = ITEMS.register("star_light", StarLight::new);
    public static final RegistryObject<Item> CSDY_SWORD = ITEMS.register("csdy_sword", CsdySword::new);






}
