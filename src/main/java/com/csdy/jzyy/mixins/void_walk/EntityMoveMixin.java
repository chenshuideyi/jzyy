package com.csdy.jzyy.mixins.void_walk;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.hasVoidWalk;

@Mixin(Entity.class)
public abstract class EntityMoveMixin {

    @Redirect(
            method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At(
                    value = "INVOKE",
                    // 目标从 FIELD 变成了 INVOKE，指向 collide 方法的调用
                    target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 voidWalk(Entity instance, Vec3 pVector) {
        if (instance instanceof Player player && hasVoidWalk(player)) {
            return pVector;
        }

        return ((EntityInvoker) instance).callCollide(pVector);
    }


}
