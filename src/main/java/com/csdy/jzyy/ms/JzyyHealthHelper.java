package com.csdy.jzyy.ms;

import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.LivingEntity;

public class JzyyHealthHelper {

    public static float getForcedHealth(LivingEntity entity) {
        if (entity != null) {
            EntityCategory category = CoreMsUtil.getCategory(entity);
            if (category == EntityCategory.csdykill) {
                return 0.0f;
            }
            if (category == EntityCategory.csdy) {
                return 20.0f;
            }
        }

        return -1.0f;
    }

}
