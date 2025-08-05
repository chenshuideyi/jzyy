package com.csdy.jzyy.mixins.void_walk;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 这是一个 Mixin Accessor 接口。
 * 它的目标是 EntityCollisionContext 类，
 * 作用是为我们生成一个公共的、合法的 getter 方法，
 * 以便读取其内部私有的 'entity' 字段。
 */
@Mixin(EntityCollisionContext.class)
public interface EntityCollisionContextAccessor {

    /**
     * @Accessor("entity") 会告诉 Mixin：
     * "请为我生成一个方法，它能返回名为 'entity' 的字段的值。"
     * Mixin 会自动处理所有细节。
     */
    @Accessor("entity")
    Entity getEntity();
}
