package com.csdy.jzyy.mixins.void_walk;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {

    /**
     * 我们声明一个与目标私有方法签名完全相同的方法。
     * &#064;Invoker("collide")  会告诉 Mixin：
     * “当这个接口的 callCollide 方法被调用时，请实际去调用那个名为 'collide' 的私有方法。”
     * <p>
     * 我们通常会用 "call" 或 "invoke" 作为前缀，以避免命名冲突。
     */
    @Invoker("collide")
    Vec3 callCollide(Vec3 pVector);
}
