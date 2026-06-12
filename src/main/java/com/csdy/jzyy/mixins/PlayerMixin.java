package com.csdy.jzyy.mixins;

//@Mixin(Player.class)
//public abstract class PlayerMixin {
//
//    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
//    private void onDrop(ItemStack pItemStack, boolean pIncludeThrowerName, CallbackInfoReturnable<Boolean> cir) {
//        if (CoreMsUtil.getCategory((Player)(Object)this) == EntityCategory.csdy) {
//            cir.setReturnValue(false); // 阻止丢弃
//        }
//    }
//
//
//}
