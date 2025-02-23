package com.csdy.item.register;

import com.csdy.ModMain;
import com.csdy.item.Death;
import com.csdy.item.sword.CsdySword;
import com.csdy.item.sword.Mtf;
import com.csdy.item.sword.Test;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HideRegister {
    public static DeferredRegister<Item> HIDE = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
    public static final RegistryObject<Item> DEATH = HIDE.register("death", Death::new);
    public static final RegistryObject<Item> TEST = HIDE.register("test", () -> new Test(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
    public static final RegistryObject<Item> MTF = HIDE.register("mtf_sword", () -> new Mtf(Tiers.DIAMOND, -2, -8F, new Item.Properties()));
    public static final RegistryObject<Item> TEST1 = HIDE.register("test_axe", () -> new com.csdy.item.axe.Test(Tiers.IRON, 2, 0, new Item.Properties()));
    public static final RegistryObject<Item> CSDY_SWORD = HIDE.register("csdy_sword", () -> new CsdySword(Tiers.DIAMOND, 3, -2.4F, new Item.Properties()));
}
