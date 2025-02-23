package com.csdy.until.Tab;

import com.csdy.ModMain;
import com.csdy.item.register.ItemRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.csdy.ModMain.MODID;
import static com.csdy.item.register.ItemRegister.SACRED_RELIC;

public class CsdyTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(ModMain.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("Csdy Sword Tabs"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(SACRED_RELIC.get()))
            .displayItems((enabledFeatures, output) -> {
                for(RegistryObject<Item> item : ItemRegister.ITEMS.getEntries()){
                        output.accept(item.get());
                }
            })
            .build());

}
