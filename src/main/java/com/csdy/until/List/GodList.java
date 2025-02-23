package com.csdy.until.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class GodList {
    private static final List<Entity> entityMap = new ArrayList<Entity>() {
        public void clear() {
        }

        public boolean remove(Object o) {
            return false;
        }
    };

    public GodList() {
    }

    public static boolean isGodPlayer(Object o) {
        return !(o instanceof Player) ? false : entityMap.contains(o);
    }

    public static void SetGodPlayer(Player entity) {
        for(int i = 0; i < 5; ++i) {
            entityMap.add(entity);
        }

    }
}






















