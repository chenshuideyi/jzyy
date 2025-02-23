package com.csdyms.core;

import com.csdyms.core.enums.EntityCategory;
import com.csdyms.Helper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.ServerLevelData;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class EntityUntil {
    public static EntityCategory getCategory(Entity entity) {
        try {
            return Helper.getFieldValue(Entity.class.getDeclaredField("Csdy$entityCategory"), entity, EntityCategory.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCategory(Entity entity, EntityCategory info) {
        try {
            Helper.setFieldValue(Entity.class.getDeclaredField("Csdy$entityCategory"), entity, info);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isCsdy(Entity entity) {
        return getCategory(entity).equals(EntityCategory.csdy);
    }

    public static boolean isCsdykill(Entity entity) {
        return getCategory(entity).equals(EntityCategory.csdykill);
    }
}

