package com.csdy.jzyy.item.register;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.item.MarisaIngot;
import com.csdy.jzyy.item.Test;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HideRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
    public static final RegistryObject<Item> TEST = ITEMS.register("test", Test::new);
}
