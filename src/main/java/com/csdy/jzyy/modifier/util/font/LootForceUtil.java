package com.csdy.jzyy.modifier.util.font;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.slf4j.Logger;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LootForceUtil {
    //我寻思这玩意能行
    private static final Logger LOGGER = LogUtils.getLogger();
    /**
     * 生成实体战利品表中的所有物品
     * @param target 目标实体
     * @return 生成的物品列表
     */
    public static List<ItemStack> generateEntityLoot(Entity target) {
        return generateEntityLoot(target, 0, false);
    }

    /**
     * 生成实体战利品表中的所有物品
     * @param target 目标实体
     * @param lootingLevel 掠夺等级
     * @param hitByPlayer 是否被玩家击中
     * @return 生成的物品列表
     */
    public static List<ItemStack> generateEntityLoot(Entity target, float lootingLevel, boolean hitByPlayer) {
        List<ItemStack> drops = new ArrayList<>();
        if (target.level() instanceof ServerLevel serverLevel) {
            List<Item> lootTableItems = parseEntityLootTable(target);
            for (Item item : lootTableItems) {
                if (item != null && item != Items.AIR) {
                    ItemStack stack = new ItemStack(item);
                    drops.add(stack);
                    generateItem(target, stack);
                }
            }
        }

        return drops;
    }

    /**
     * 生成指定物品到实体位置
     * @param entity 目标实体
     * @param itemStack 物品栈
     * @return 是否生成成功
     */
    private static boolean generateItem(Entity entity, ItemStack itemStack) {
        if (entity == null || entity.isRemoved() || itemStack.isEmpty()) {
            return false;
        }

        try {
            entity.spawnAtLocation(itemStack, 0.5F);

            return true;
        } catch (Exception e) {

            return false;
        }
    }

    /**
     * 解析实体的战利品表，返回所有可能的物品
     * @param entity 目标实体
     * @return 物品列表
     */
    public static List<Item> parseEntityLootTable(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {

            return Collections.emptyList();
        }

        ResourceLocation lootTableId = getEntityLootTableId(entity);
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(lootTableId);

        if (lootTable == LootTable.EMPTY) {
            LOGGER.debug("[LootForceUtil] 实体 {} 没有战利品表", EntityType.getKey(entity.getType()));
            return Collections.emptyList();
        }

        return parseLootTable(lootTable);
    }

    /**
     * 解析指定的战利品表，返回所有可能的物品
     * @param lootTable 战利品表
     * @return 物品列表
     */
    public static List<Item> parseLootTable(LootTable lootTable) {
        List<Item> items = new ArrayList<>();
        try {
            Field poolsField = getFirstMatchedField(LootTable.class, new String[]{"f_79182_", "f_79109_", "pools"});
            if (poolsField == null) {

                return items;
            }
            poolsField.setAccessible(true);
            Object poolsObj = poolsField.get(lootTable);

            List<LootPool> lootPools = new ArrayList<>();
            if (poolsObj instanceof List<?>) {
                lootPools.addAll((List<LootPool>) poolsObj);
            } else if (poolsObj instanceof LootPool[]) {
                Collections.addAll(lootPools, (LootPool[]) poolsObj);
            }

            for (LootPool pool : lootPools) {
                items.addAll(parseLootPool(pool));
            }
            // 去重并返回
            return items.stream().distinct().collect(Collectors.toList());
        } catch (Exception e) {

            return items;
        }
    }



    /**
     * 解析LootPool，返回所有可能的物品
     * @param pool LootPool
     * @return 物品列表
     */
    private static List<Item> parseLootPool(LootPool pool) {
        List<Item> items = new ArrayList<>();

        try {
            // 反射获取LootPool的entries字段
            Field entriesField = getFirstMatchedField(LootPool.class, new String[]{"f_79023_", "f_79071_", "f_79067_", "entries"});
            if (entriesField == null) {

                return items;
            }

            entriesField.setAccessible(true);
            Object entriesObj = entriesField.get(pool);

            List<LootPoolEntryContainer> entries = new ArrayList<>();
            if (entriesObj instanceof List<?>) {
                entries.addAll((List<LootPoolEntryContainer>) entriesObj);
            } else if (entriesObj instanceof LootPoolEntryContainer[]) {
                Collections.addAll(entries, (LootPoolEntryContainer[]) entriesObj);
            }

            for (LootPoolEntryContainer entry : entries) {
                if (entry == null) continue;

                if (entry instanceof LootItem lootItem) {
                    Item item = getLootItemFromLootEntry(lootItem);
                    if (item != null && item != Items.AIR) {
                        items.add(item);
                    }
                } else if (hasChildrenField(entry)) {

                    items.addAll(parseCompoundEntry(entry));
                }
            }

        } catch (Exception e) {

        }

        return items;
    }

    /**
     * 递归解析嵌套的战利品条目
     */
    private static List<Item> parseCompoundEntry(LootPoolEntryContainer entry) {
        List<Item> items = new ArrayList<>();

        try {
            Field childrenField = getFirstMatchedField(entry.getClass(), new String[]{"f_79285_", "f_79481_", "children"});
            if (childrenField == null) {
                return items;
            }

            childrenField.setAccessible(true);
            Object childrenObj = childrenField.get(entry);

            List<LootPoolEntryContainer> children = new ArrayList<>();
            if (childrenObj instanceof List<?>) {
                children.addAll((List<LootPoolEntryContainer>) childrenObj);
            } else if (childrenObj instanceof LootPoolEntryContainer[]) {
                Collections.addAll(children, (LootPoolEntryContainer[]) childrenObj);
            }


            for (LootPoolEntryContainer childEntry : children) {
                if (childEntry == null) continue;

                if (childEntry instanceof LootItem lootItem) {
                    Item item = getLootItemFromLootEntry(lootItem);
                    if (item != null && item != Items.AIR) {
                        items.add(item);
                    }
                } else if (hasChildrenField(childEntry)) {
                    items.addAll(parseCompoundEntry(childEntry));
                }
            }

        } catch (Exception e) {

        }

        return items;
    }

    /**
     * 从LootItem中提取对应的Item实例
     */
    private static Item getLootItemFromLootEntry(LootItem lootItem) {
        try {
            Field itemField = getFirstMatchedField(LootItem.class, new String[]{"f_79564_", "item"});
            if (itemField != null) {
                itemField.setAccessible(true);
                return (Item) itemField.get(lootItem);
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 判断战利品条目是否包含子条目
     */
    private static boolean hasChildrenField(LootPoolEntryContainer entry) {
        return getFirstMatchedField(entry.getClass(), new String[]{"f_79285_", "f_79481_", "children"}) != null;
    }


    private static Field getFirstMatchedField(Class<?> clazz, String[] possibleFieldNames) {
        if (clazz == null || possibleFieldNames == null || possibleFieldNames.length == 0) {
            return null;
        }


        for (String fieldName : possibleFieldNames) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {

            }
        }


        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && !superClass.equals(Object.class)) {
            for (String fieldName : possibleFieldNames) {
                try {
                    return superClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                }
            }
            superClass = superClass.getSuperclass();
        }

        return null;
    }

    /**
     * 获取实体对应的战利品表ID
     */
    private static ResourceLocation getEntityLootTableId(Entity entity) {
        ResourceLocation registryName = EntityType.getKey(entity.getType());
        if (registryName != null) {
            String namespace = registryName.getNamespace();
            String path = registryName.getPath();
            return new ResourceLocation(namespace, "entities/" + path);
        } else {
            String descriptionId = entity.getType().getDescriptionId().replace("entity.", "");
            String lootTablePath = descriptionId.contains(":")
                    ? descriptionId.replace(":", ":entities/")
                    : "minecraft:entities/" + descriptionId;
            return new ResourceLocation(lootTablePath);
        }
    }
}
