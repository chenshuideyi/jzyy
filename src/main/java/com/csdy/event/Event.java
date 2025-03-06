package com.csdy.event;

import com.csdy.ModMain;
import com.csdy.diadema.DiademaRegister;
import com.csdy.diadema.warden.WardenDiadema;
import com.csdy.frames.diadema.Diadema;
import com.csdy.item.register.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Event {

    private static boolean isWarden(Object o) {
        return o instanceof Player player
                && DiademaRegister.WARDEN.get().isAffected(player)
                && !(WardenDiadema.WhiteList.contains(player));
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent e) {
        LivingEntity living = e.getEntity();
        if (living instanceof Player player) {
            CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            String firstdeath = "first_death";
            if (!tag.getBoolean(firstdeath)) {
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

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (isWarden(mc.player)) {
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            GuiGraphics guiGraphics = event.getGuiGraphics();
            // 绘制全屏黑色矩形
            guiGraphics.fill(0, 0, width, height, 0xFF000000); // ARGB格式：0x80表示50%透明度，000000表示黑色
        }

    }


}


