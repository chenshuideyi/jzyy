package com.csdy.jzyy.mixins;


import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "shrink(I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelShrinkForCsdyPlayer(int amount, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (stack.getItem() instanceof DiggerItem) {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                for (Player player : level.players()) {
                    if (player.getInventory().contains(stack.getItem().getDefaultInstance())
                            && CoreMsUtil.getCategory(player) == EntityCategory.csdy) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }

}
