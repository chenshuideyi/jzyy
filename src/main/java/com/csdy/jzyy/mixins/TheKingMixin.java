package com.csdy.jzyy.mixins;

import crazynessawakened.procedures.CoinDiesProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

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

