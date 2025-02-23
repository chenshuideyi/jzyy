package com.csdy.rainbowflash;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

//public class RainbowLightingEntity extends LightningBolt {
//    private int life;
//    public long seed;
//    private int flashes;


//
//    public RainbowLightingEntity(EntityType<Entity> entityEntityType, Level level) {
//        super((EntityType<? extends LightningBolt>) entityEntityType, level);
////        this.noCulling = true;
////        this.life = 5;
////        this.flashes = 30;
////        this.seed = this.random.nextLong();
//    }
//
//    public RainbowLightingEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
//        this(EntityRegister.RAINBOW_LIGHTING.get(),level);
//
//    }




//    public RainbowLightingEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
//        super(spawnEntity,level);
//        this.noCulling = true;
//        this.life = 5;
//        this.flashes = 30;
//        this.seed = this.random.nextLong();
//    }

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


//}

