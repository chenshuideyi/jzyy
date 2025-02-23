package com.csdy.rainbowflash;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

//public class RainbowLightingEntity2 extends LightningBolt {
//    private int life;
//    public long seed;
//    private int flashes;
//
//    public RainbowLightingEntity2(PlayMessages.SpawnEntity packet, Level world) {
//        this((EntityType)EntityRegister.RAINBOW_LIGHTING2.get(), world);
//    }
//
//    public void tick() {
//        if (this.life == 5 && this.level().isClientSide()) {
//            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
//            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
//        }
//
//        --this.life;
//        if (this.life < 0) {
//            if (this.flashes == 0) {
//                this.discard();
//            } else {
//                --this.flashes;
//                this.seed = this.random.nextLong();
//            }
//        }
//
//    }
//
//    public @NotNull SoundSource getSoundSource() {
//        return SoundSource.WEATHER;
//    }
//
//    public RainbowLightingEntity2(EntityType<RainbowLightingEntity2> type, Level world) {
//        super(type, world);
//        this.noCulling = true;
//        this.life = 5;
//        this.flashes = 30;
//        this.seed = this.random.nextLong();
//    }
//}

