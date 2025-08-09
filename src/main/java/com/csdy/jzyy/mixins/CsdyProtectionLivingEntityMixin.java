package com.csdy.jzyy.mixins;

import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = LivingEntity.class, priority = Integer.MAX_VALUE)
public abstract class CsdyProtectionLivingEntityMixin {

    @Unique
    private EntityCategory Csdy$entityCategory = EntityCategory.normal;

}
