package com.csdy.jzyy.ms;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("unused")
public final class CoreMethod {
    public static float getHealth(LivingEntity entity) {
        return switch (CoreMsUtil.getCategory(entity)) {
            case csdy -> 20.0F;
            case csdykill -> 0.0F;
            case normal -> entity.getHealth();
        };
    }

    public static int getDeathTime(LivingEntity entity) {
        return switch (CoreMsUtil.getCategory(entity)) {
            case csdy -> 0;
            case csdykill, normal -> entity.deathTime;
        };
    }

//    public static int getHurtTime(LivingEntity entity) {
//        return switch (EntityUntil.getCategory(entity)) {
//            case csdy -> 0;
//            case csdykill, normal -> entity.hurtTime;
//        };
//    }
//
//    public static int getHurtDuration(LivingEntity entity) {
//        return switch (EntityUntil.getCategory(entity)) {
//            case csdy -> 0;
//            case csdykill, normal -> entity.hurtDuration;
//        };
//    }
//
//    public static Entity.RemovalReason getRemovalReason(Entity entity) {
//        return switch (CoreMsUtil.getCategory(entity)) {
//            case csdy -> null;
//            case csdykill -> Entity.RemovalReason.KILLED;
//            case normal -> entity.removalReason;
//        };
//    }
//
    public static boolean isRemoved(Entity entity) {
        return switch (CoreMsUtil.getCategory(entity)) {
            case csdy -> false;
            case csdykill -> true;
            case normal -> entity.isRemoved();
        };
    }

//    public static boolean isAlive(Entity entity) {
//        return switch (CoreMsUtil.getCategory(entity)) {
//            case csdy -> true;
//            case csdykill -> false;
//            case normal -> entity.isAlive();
//        };
//    }

//    public static boolean shouldDestroy(Entity.RemovalReason reason) {
//        return reason != null && reason.shouldDestroy();
//    }
//
//    public static boolean shouldSave(Entity.RemovalReason reason) {
//        return reason == null || reason.shouldSave();
//    }

//    public static boolean isDeadOrDying(LivingEntity entity, boolean originalResult) {
//        // originalResult 已经是其他 Mixin 处理后的值
//        return switch (EntityUntil.getCategory(entity)) {
//            case csdy -> false;       // 强制返回 false
//            case csdykill -> true;     // 强制返回 true
//            case normal -> originalResult; // 保留 Mixin 的结果
//        };
//    }
//
//    public static float getMaxHealth(LivingEntity living, Float returnValue) {
//        if (EntityUntil.isCsdy(living)){
//            return 20.0f;
//        } if (EntityUntil.isCsdykill(living)){
//            return 0.0f;
//        }
//        return (float)living.getAttributeValue(Attributes.MAX_HEALTH);
//    }
//
//    public static float getMaxHealth(LivingEntity living) {
//        if (EntityUntil.isCsdy(living)){
//            return 20.0f;
//        } if (EntityUntil.isCsdykill(living)){
//            return 0.0f;
//        }
//        return (float)living.getAttributeValue(Attributes.MAX_HEALTH);
//    }
}
