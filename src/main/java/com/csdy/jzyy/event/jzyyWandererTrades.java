package com.csdy.jzyy.event;

import com.csdy.jzyy.item.ItemForEmeraldsTrade;
import com.csdy.jzyy.item.register.ItemRegister;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.List;
import java.util.Random;
import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class jzyyWandererTrades {

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {

        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        jzyyTrades(genericTrades, rareTrades);
    }

    private static void jzyyTrades(List<VillagerTrades.ItemListing> genericTrades,
                                   List<VillagerTrades.ItemListing> rareTrades) {

        Random random = new Random();
        if (random.nextDouble() < 0.1) {
            rareTrades.add(new ItemForEmeraldsTrade(
                    new ItemStack(ItemRegister.DOT.get()),
                    64,                                     // 绿宝石价格
                    1,                                     // 物品数量
                    3,                                     // 最大使用次数
                    1145                                   // 经验值
            ));
        }
    }
}