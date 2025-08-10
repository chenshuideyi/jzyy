package com.csdy.jzyy.mixins;

import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.function.Predicate;

@Mixin(targets = "net.minecraft.world.entity.player.Inventory")
public abstract class InventoryMixin {
    @Inject(
            method = "clearOrCountMatchingItems",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onClearOrCountMatchingItems(
            Predicate<ItemStack> predicate,
            int maxCount,
            Container container,
            CallbackInfoReturnable<Integer> cir
    ) {
        // 通过反射获取关联的Player
        try {
            Field playerField = ((Object)this).getClass().getDeclaredField("player");
            playerField.setAccessible(true);
            Player player = (Player)playerField.get(this);

            if (player != null && CoreMsUtil.getCategory(player) == EntityCategory.csdy) {
                cir.setReturnValue(0);
                cir.cancel();
            }
        } catch (Exception e) {
            // 处理异常
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"), cancellable = true)
    private void modernClearProtection(CallbackInfo ci) {
        ci.cancel();
    }

}




