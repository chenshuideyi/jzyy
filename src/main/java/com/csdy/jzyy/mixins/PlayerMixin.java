package com.csdy.jzyy.mixins;

import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
