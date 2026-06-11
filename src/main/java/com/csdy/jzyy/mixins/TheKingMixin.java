package com.csdy.jzyy.mixins;

import crazynessawakened.procedures.CoinDiesProcedure;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CoinDiesProcedure.class)
public class TheKingMixin {

    @Inject(
            method = "getRngItem()Lnet/minecraft/world/item/Item;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void injectGetRngItem(CallbackInfoReturnable<Item> cir) {
        cir.setReturnValue(Items.AIR);
        cir.cancel();
    }
}

