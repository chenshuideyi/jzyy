package com.csdy.jzyy.test;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DeadLists {



    private static final List<Entity> Death = new ArrayList<>(){
        public void clear() {}

        public boolean remove(Object o) {
            return false;
        }
    };


    private static final List<Entity> list = new ArrayList();

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

    public static List<Entity> getAllTrackedEntities() {
        // 使用 Set 来合并两个列表并自动去重，然后转换为 List
        Set<Entity> allEntities = new HashSet<>();
        allEntities.addAll(list);
        allEntities.addAll(Death); // Death 列表可能包含与 list 重复的实体，HashSet 会处理去重
        System.out.println(allEntities);
        // 将 Set 转换回 ArrayList 并返回
        return new ArrayList<>(allEntities);
    }
}