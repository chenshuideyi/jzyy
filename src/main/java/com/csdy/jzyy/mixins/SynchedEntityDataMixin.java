package com.csdy.jzyy.mixins;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.csdy.jzyy.util.LivingEntityUtil.*;


@Mixin(value = SynchedEntityData.class, priority = 1)
public abstract class SynchedEntityDataMixin {
    @Final
    @Shadow
    private Entity entity;

    /**
     * 拦截 set 方法：当外部逻辑试图修改 DATA_HEALTH_ID 时
     *     对于其它 LivingEntity，如果存在绝对切割记录，
     *     则将写入的健康值限定为 Math.min(外部值, 绝对切割计算后的健康值)。
     */
    @Inject(
        method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    public <T> void onSet(EntityDataAccessor<T> key, T value, CallbackInfo ci) {
        // 仅处理血量字段
        if (key == LivingEntity.DATA_HEALTH_ID && this.entity instanceof LivingEntity living) {
            float destructionHealth = getAbsoluteSeveranceHealth(living);
            if (!Float.isNaN(destructionHealth)) {
                if (value instanceof Float incomingHealth) {
                    float forcedHealth = Math.min(incomingHealth, destructionHealth);
                    if (!WriteFlag.isInternalWrite()) {
                        WriteFlag.beginWrite();
                        try {
                            living.getEntityData().set((EntityDataAccessor<Float>) key, forcedHealth);
                        } finally {
                            WriteFlag.endWrite();
                        }
                        ci.cancel();
                    }
                }
            }
        }
    }

    /**
     * 拦截 get 方法：当读取 DATA_HEALTH_ID 时，
     *     如果存在绝对切割记录，则返回实际血量与绝对切割计算值中的较小值，
     *     以确保外界看到的血量不高于绝对切割伤害预期。
     */

    @Redirect(
            method = "get(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;getValue()Ljava/lang/Object;"
            ),
            require = 1
    )
    public <T> Object onGetValue(SynchedEntityData.DataItem<T> dataItem, EntityDataAccessor<T> key) {
        if (key == LivingEntity.DATA_HEALTH_ID && this.entity instanceof LivingEntity living) {
            float destructionHealth = getAbsoluteSeveranceHealth(living);
            if (!Float.isNaN(destructionHealth)) {
                float actual = (float) dataItem.getValue();
                float forcedView = Math.min(actual, destructionHealth);
                return forcedView;
            }
        }
        return dataItem.getValue();
    }



//    ///哎bro这不是递归了吗
//    /// 哈！看我注入get
//        @Inject(
//        method = "get(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;",
//        at = @At("HEAD"),
//        cancellable = true
//    )
//    public <T> void onGet(EntityDataAccessor<T> key, CallbackInfoReturnable<T> cir) {
//        if (key == LivingEntity.DATA_HEALTH_ID && this.entity instanceof LivingEntity living) {
//            float destructionHealth = getAbsoluteSeveranceHealth(living);
//            if (!Float.isNaN(destructionHealth)) {
//                float actual = living.getHealth();
//                float forcedView = Math.min(actual, destructionHealth);
//                cir.setReturnValue((T)(Object)forcedView);
//            }
//        }
//    }
}
