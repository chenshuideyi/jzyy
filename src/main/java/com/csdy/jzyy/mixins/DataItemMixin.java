package com.csdy.jzyy.mixins;

import com.csdy.jzyy.ms.util.LivingEntityUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = SynchedEntityData.DataItem.class, priority = 1)
public abstract class DataItemMixin<T> {

    @Shadow
    public T value;
    @Redirect(
        method = "setValue(Ljava/lang/Object;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;value:Ljava/lang/Object;",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void onValueSet(SynchedEntityData.DataItem<T> dataItem, T newValue) {
        if (!LivingEntityUtil.WriteFlag.isInternalWrite()) {
            return;
        }
        dataItem.value = newValue;
    }
}
