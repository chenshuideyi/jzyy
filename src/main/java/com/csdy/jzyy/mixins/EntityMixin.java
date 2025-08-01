package com.csdy.jzyy.mixins;


import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Entity.class, priority = Integer.MAX_VALUE)
public abstract class EntityMixin {

    @Unique
    private EntityCategory Csdy$entityCategory = EntityCategory.normal;

}
