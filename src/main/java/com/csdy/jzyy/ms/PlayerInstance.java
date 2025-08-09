package com.csdy.jzyy.ms;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerInstance {
    public ServerPlayer serverInstance;
    public LocalPlayer clientInstance;

    public PlayerInstance(){
    }

    public void put(Player plr){
        if (plr instanceof ServerPlayer sp){
            this.serverInstance = sp;
        }else if (plr instanceof LocalPlayer lp){
            this.clientInstance = lp;
        }
    }

    public void each(Consumer<? super Player> consumer){
        List<Player> list = new ArrayList<>();
        if (serverInstance!=null)list.add(serverInstance);
        if (clientInstance!=null)list.add(clientInstance);
        list.forEach(consumer);
    }
}
