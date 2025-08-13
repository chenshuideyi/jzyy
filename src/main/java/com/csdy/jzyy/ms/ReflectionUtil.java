package com.csdy.jzyy.ms;

import net.minecraft.world.entity.Entity;
import java.lang.reflect.Method;

import static com.csdy.jzyy.ms.util.MsUtil.KillEntity;

public class ReflectionUtil {
    private static Method killEntityMethod = null;

    public static void invokeKillEntity(Entity target) {
        try {
            if (killEntityMethod == null) {
                Class<?> ssUtilClass = Class.forName("plz.lizi.supersteve.init.SSUtil");
                killEntityMethod = ssUtilClass.getDeclaredMethod("killEntity", Entity.class);
                killEntityMethod.setAccessible(true);
            }
            killEntityMethod.invoke(null, target);
        } catch (Exception e) {
            e.printStackTrace();
            KillEntity(target);
        }
    }
}
