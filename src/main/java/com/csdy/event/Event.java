package com.csdy.event;

import com.csdy.ModMain;
import com.csdy.effect.Scared;
import com.csdy.effect.register.EffectRegister;
import com.csdy.item.register.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;


import static com.csdy.diadema.warden.Warden.isWarden;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Event {

    @SubscribeEvent
    public static void death(LivingDeathEvent e) {
        LivingEntity living = e.getEntity();
        if (living instanceof Player) {
            Player player = (Player) living;
            CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            String firstdeath = "first_death";
            if (!tag.getBoolean(firstdeath)){
                player.setHealth(player.getMaxHealth());
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ItemRegister.BROKEN_SACRED_RELIC.get()));
                player.displayClientMessage(Component.literal("§n§4一把来自古代的遗物突然过来保护了你!"), false);
                player.getPersistentData().getBoolean("first_death");
                tag.putBoolean(firstdeath, true);
                e.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
                e.setCanceled(true);
            }
        }
    }

//    @SubscribeEvent
//    public static void onRenderGui(RenderGuiEvent.Post event) {
//        // 获取 GuiGraphics 实例
//        GuiGraphics guiGraphics = event.getGuiGraphics();
//        // 获取屏幕宽度和高度
//        int width = event.getWindow().getGuiScaledWidth();
//        int height = event.getWindow().getGuiScaledHeight();
//
//        // 绘制灰色矩形覆盖整个屏幕
////        guiGraphics.fill(0, 0, width, height, 0xFF000000); // ARGB格式：0x80表示50%透明度，000000表示黑色
//    }


    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (isWarden(mc.player)){
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        GuiGraphics guiGraphics = event.getGuiGraphics();
                // 绘制全屏黑色矩形
        guiGraphics.fill(0, 0, width, height, 0xFF000000); // ARGB格式：0x80表示50%透明度，000000表示黑色
        }

    }



}
//@SubscribeEvent
//    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
//        if (e.getEntity() instanceof ServerPlayer serverPlayer) {
//            Networking.sendToPlayerClient(new PacketInitDocs(), serverPlayer);
//            boolean isContributor = Rewards.CONTRIBUTORS.contains(serverPlayer.getUUID());
//            if (isContributor) {
//                Networking.sendToPlayerClient(new PacketJoinedServer(true), serverPlayer);
//            }
//        }
//        CompoundTag tag = e.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
//        String book_tag = "an_book_";
//        if (!tag.getBoolean(book_tag)) {
//            Player entity = e.getEntity();
//            ItemHandlerHelper.giveItemToPlayer(entity, new ItemStack(ItemRegister.BROKEN_SACRED_RELIC.get()));
//            tag.putBoolean(book_tag, true);
////            e.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
////        }
//    }

