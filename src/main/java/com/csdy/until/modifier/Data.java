package com.csdy.until.modifier;

import com.csdy.ModMain;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class Data {
    @SubscribeEvent
    public static void data(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput= generator.getPackOutput();


        generator.addProvider(event.includeServer(), new ModifiersProvider(packOutput));

    }
}
