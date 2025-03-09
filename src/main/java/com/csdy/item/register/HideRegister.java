package com.csdy.item.register;

import com.csdy.ModMain;
import com.csdy.item.sword.Web_13234;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HideRegister {
    public static DeferredRegister<Item> HIDE = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
    public static final RegistryObject<Item> WEB_13234 = HIDE.register("web_13234", () -> new Web_13234(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
}
