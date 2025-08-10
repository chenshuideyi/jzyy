package com.csdy.jzyy.ms;

import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

import java.lang.invoke.MethodType;
import java.util.Objects;

public class CoreMsUtil {
    private static final MethodHandle GET_CATEGORY_HANDLE;
    private static final MethodHandle SET_CATEGORY_HANDLE;

    static {
        try {
            Field categoryField = LivingEntity.class.getDeclaredField("Csdy$entityCategory");
            categoryField.setAccessible(true);

            Objects.requireNonNull(PLZBase.LOOKUP, "MethodHandles.Lookup不可用");

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

    public static EntityCategory getCategory(LivingEntity entity) {
        try {
            EntityCategory category = (EntityCategory) GET_CATEGORY_HANDLE.invokeExact(entity);
            return category != null ? category : EntityCategory.normal;
        } catch (Throwable e) {
            throw new RuntimeException("获取实体分类失败 - 实体: " + entity, e);
        }
    }

    public static void setCategory(LivingEntity entity, EntityCategory info) {
        try {
            SET_CATEGORY_HANDLE.invokeExact(entity, info);
        } catch (Throwable e) {
            throw new RuntimeException("设置实体分类失败 - 实体: " + entity + ", 分类: " + info, e);
        }
    }
}
