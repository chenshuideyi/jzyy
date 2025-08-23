package com.csdy.jzyy.ms;

import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.csdy.jzyy.ms.util.MsUtil.KillEntity;

@SuppressWarnings("unused")
public final class CoreMethod {

    public static float getHealth(LivingEntity entity) {
        return switch (CoreMsUtil.getCategory(entity)) {
            case csdy -> 20.0F;
            case csdykill -> 0.0F;
            case normal -> {
                float health = entity.getHealth();
                if (Float.isNaN(health)) {
                    System.err.println("尝试修复一个假死，实体为" + entity.getClass());
                    if ((entity instanceof Player player)){
                        yield 0.0F;
                    }
                    KillEntity(entity);
                    yield 0.0F;
                } else {
                    yield health;
                }
            }
        };
    }

    public static boolean isDeadOrDying(LivingEntity living) {
        return switch (CoreMsUtil.getCategory(living)) {
            case csdy -> false;
            case csdykill -> true;
            case normal -> living.isDeadOrDying();
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

//    public static boolean isRemoved(Entity entity) {
//        return switch (CoreMsUtil.getCategory(entity)) {
//            case csdy -> false;
//            case csdykill -> true;
//            case normal -> entity.isRemoved();
//        };
//    }

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



}
