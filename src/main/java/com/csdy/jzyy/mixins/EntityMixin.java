package com.csdy.jzyy.mixins;


//import com.csdy.jzyy.core.EntityUntil;
//import com.csdy.jzyy.core.enums.EntityCategory;
//import net.minecraft.world.entity.Entity;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(value = Entity.class, priority = Integer.MAX_VALUE)
//public abstract class EntityMixin {
//
//    @Unique
//    private EntityCategory Csdy$entityCategory = EntityCategory.normal;
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    private void tick(CallbackInfo ci) {
////        if (GodList.isGodPlayer(this)) EntityUntil.setCategory((Entity) (Object)this, EntityCategory.csdy);
//    }
//
//    @Inject(
//            method = "isRemoved",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void isRemoved(CallbackInfoReturnable<Boolean> cir) {
//        if ((Object)this instanceof Entity entity && EntityUntil.isCsdykill(entity)) cir.setReturnValue(true);
//    }
//}
