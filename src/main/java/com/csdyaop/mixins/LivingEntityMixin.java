package com.csdyaop.mixins;

import com.csdy.item.register.ItemRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {

    @Inject(
            method = "dropFromLootTable",
            at = @At("TAIL"),
            cancellable = true
    )
    private void Drop(DamageSource p_21021_, boolean p_21022_, CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (living instanceof Warden) {
            Level level = living.level();

            ItemStack warden = new ItemStack(ItemRegister.WARDEN_HEART.get());

            ItemEntity itemEntity = new ItemEntity(level, living.getX(), living.getY(), living.getZ(), warden);
            living.level.addFreshEntity(itemEntity);
        } else if (living instanceof EnderDragon) {
            Level level = living.level();

            ItemStack ender = new ItemStack(ItemRegister.ENDERDRAGON_HEART.get());

            ItemEntity itemEntity = new ItemEntity(level, living.getX(), living.getY(), living.getZ(), ender);
            living.level.addFreshEntity(itemEntity);
        }else if (living instanceof IronGolem) {
            Level level = living.level();

            ItemStack irongolem = new ItemStack(ItemRegister.NEURAL_SENSORS.get());

            ItemEntity itemEntity = new ItemEntity(level, living.getX(), living.getY(), living.getZ(), irongolem);
            living.level.addFreshEntity(itemEntity);
        }
    }



}
