package com.csdy.until.List;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;


public class DeadLists {



    private static final List<Entity> Death = new ArrayList<>(){
        public void clear() {}

        public boolean remove(Object o) {
            return false;
        }
    };


    private static final List<Entity> list = new ArrayList();
    private static final List<String> list1 = new ArrayList();
    public static boolean isban(Object o) {
        return o != null && o instanceof Entity ? list1.contains(o.getClass().getName()) : false;
    }

    public static void addbanEntity(Entity entity) {
        list1.add(entity.getClass().getName());
    }


    public static boolean isEntity(Object o) {
        return o != null && o instanceof Entity ? (list.contains(o) || Death.contains(o)) : false;
    }

    public static void addEntityToDeadList(Entity entity) {
        list.add(entity);
        Death.add(entity);
    }



    public static void SetDeaded(Entity player) {
        Death.add(player);
    }

}