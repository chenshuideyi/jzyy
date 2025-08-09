package com.csdy.jzyy.ms;

import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

import java.lang.invoke.MethodType;
import java.util.Objects;

public class CoreMsUtil {
    // 缓存MethodHandle以提高性能
    private static final MethodHandle GET_CATEGORY_HANDLE;
    private static final MethodHandle SET_CATEGORY_HANDLE;

    static {
        try {
            // 获取LivingEntity类的私有字段
            Field categoryField = LivingEntity.class.getDeclaredField("Csdy$entityCategory");
            categoryField.setAccessible(true);

            // 确保PLZBase.LOOKUP不为null
            Objects.requireNonNull(PLZBase.LOOKUP, "MethodHandles.Lookup不可用");

            // 创建类型安全的MethodHandle
            GET_CATEGORY_HANDLE = PLZBase.LOOKUP.unreflectGetter(categoryField)
                    .asType(MethodType.methodType(EntityCategory.class, LivingEntity.class));

            SET_CATEGORY_HANDLE = PLZBase.LOOKUP.unreflectSetter(categoryField)
                    .asType(MethodType.methodType(void.class, LivingEntity.class, EntityCategory.class));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("未找到Csdy$entityCategory字段", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问Csdy$entityCategory字段", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("PLZBase.LOOKUP未初始化", e);
        }
    }

    /**
     * 获取实体的分类信息
     * @param entity 目标实体，必须为LivingEntity实例
     * @return 实体分类
     * @throws IllegalArgumentException 如果实体不是LivingEntity实例
     * @throws RuntimeException 如果访问字段失败
     */
    public static EntityCategory getCategory(Entity entity) {
        try {
            if (!(entity instanceof LivingEntity)) {
                throw new IllegalArgumentException("实体必须是LivingEntity实例");
            }
            return (EntityCategory) GET_CATEGORY_HANDLE.invokeExact((LivingEntity)entity);
        } catch (Throwable e) {
            throw new RuntimeException("获取实体分类失败 - 实体: " + entity, e);
        }
    }

    /**
     * 设置实体的分类信息
     * @param entity 目标实体，必须为LivingEntity实例
     * @param info 要设置的分类信息
     * @throws IllegalArgumentException 如果实体不是LivingEntity实例
     * @throws RuntimeException 如果设置字段失败
     */
    public static void setCategory(Entity entity, EntityCategory info) {
        try {
            if (!(entity instanceof LivingEntity)) {
                throw new IllegalArgumentException("实体必须是LivingEntity实例");
            }
            SET_CATEGORY_HANDLE.invokeExact((LivingEntity)entity, info);
        } catch (Throwable e) {
            throw new RuntimeException("设置实体分类失败 - 实体: " + entity + ", 分类: " + info, e);
        }
    }
}
