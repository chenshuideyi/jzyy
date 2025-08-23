package com.csdy.jzyy.ms;

import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.LivingEntity;

public class JzyyHealthHelper {

    public static float getForcedHealth(LivingEntity entity) {
        if (entity != null) {
            EntityCategory category = CoreMsUtil.getCategory(entity);
            if (category == EntityCategory.csdykill) {
                return 0.0f; // 对应 csdykill 类别
            }
            if (category == EntityCategory.csdy) {
                return 20.0f; // 对应 csdy 类别
            }
        }
        // 对于所有其他情况，返回一个特殊信号值
        return -1.0f;
    }

}
