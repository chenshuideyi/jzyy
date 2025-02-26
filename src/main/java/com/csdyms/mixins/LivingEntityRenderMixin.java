package com.csdyms.mixins;

import com.csdy.until.List.GodList;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


//@OnlyIn(Dist.CLIENT)
@Mixin(value = LivingEntityRenderer.class,priority = Integer.MAX_VALUE)
public abstract class LivingEntityRenderMixin {

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Entity;)Z",at = @At("RETURN"),cancellable = true)
    private void shouldShowName1(Entity par1, CallbackInfoReturnable ci){
        if (GodList.isGodPlayer(par1)) ci.setReturnValue(Boolean.TRUE);
    }


}
