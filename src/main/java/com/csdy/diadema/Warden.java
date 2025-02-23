package com.csdy.diadema;


import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Warden {
    private static final List<Player> Warden = new ArrayList<>();

    public static boolean isWarden(Object o) {
        return o instanceof Player && Warden.contains(o);
    }

    public static void addWarden(Player player) {
       Warden.add(player);
    }


    public static void removeWarden(Player player) {
        Warden.remove(player);
    }
}
