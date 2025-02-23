package com.csdy.until;

import com.csdy.until.method.KillPlayerMethod;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkEvent;

import java.awt.*;

public class Util {
    public static <T, E> void fieldSetField(T instance, Class<? super T> cls, String fieldName, E val, String srg) {
        String[] remap = new String[]{srg, fieldName};
        String name = SharedConstants.IS_RUNNING_IN_IDE ? remap[1] : remap[0];
        try {
            ObfuscationReflectionHelper.setPrivateValue(cls, instance, val, name);
        } catch (Exception ignored) {
        }
    }
    public static void DeathPlayer(ServerPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        PlayerList list = mc.player.getServer().getPlayerList();
        ServerPlayer serverPlayer = list.getPlayer(mc.player.getUUID());
            serverPlayer.hurt(new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL)), Float.MAX_VALUE);
        }
//        replaceClass(mc.player, KillPlayerMethod.class);

    public static void Gamemode1(ServerPlayer player) {
        Minecraft mc = Minecraft.getInstance();
            player.setGameMode(GameType.CREATIVE);
            player.displayClientMessage(Component.literal("已将自己的游戏模式改为创造模式"), false);

    }
    public static void replaceClass(Object object, Class<?> targetClass) {
        if (object == null)
            throw new NullPointerException("object==null");
        if (targetClass == null)
            throw new NullPointerException("targetClass==null");
        try {
            int klass_ptr = UnsafeAccess.UNSAFE.getIntVolatile(UnsafeAccess.UNSAFE.allocateInstance(targetClass), 8L);
            UnsafeAccess.UNSAFE.putIntVolatile(object, 8L, klass_ptr);
        } catch (InstantiationException ex) {

        }
    }
}
