package com.csdy.jzyy.mixins;

//@Mixin(targets = "net.minecraft.world.entity.player.Inventory")
//public abstract class InventoryMixin {
//    @Inject(
//            method = "clearOrCountMatchingItems",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void onClearOrCountMatchingItems(
//            Predicate<ItemStack> predicate,
//            int maxCount,
//            Container container,
//            CallbackInfoReturnable<Integer> cir
//    ) {
//        // 通过反射获取关联的Player
//        try {
//            Field playerField = ((Object)this).getClass().getDeclaredField("player");
//            playerField.setAccessible(true);
//            Player player = (Player)playerField.get(this);
//
//            if (player != null && CoreMsUtil.getCategory(player) == EntityCategory.csdy) {
//                cir.setReturnValue(0);
//                cir.cancel();
//            }
//        } catch (Exception e) {
//            // 处理异常
//        }
//    }
//
//    @Inject(method = "clearContent", at = @At("HEAD"), cancellable = true)
//    private void modernClearProtection(CallbackInfo ci) {
//        ci.cancel();
//    }
//
//}




