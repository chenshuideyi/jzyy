package com.csdy.test;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class ColorLightningBolt2 extends LightningBolt {

    public ColorLightningBolt2(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType) EntityRegister.RAINBOW_LIGHTING2.get(), world);
    }

    public ColorLightningBolt2(EntityType<Entity> entityEntityType, Level level) {
        super(EntityType.LIGHTNING_BOLT, level);
        this.noCulling = true;
        this.life = 5;
        this.flashes = 30;
        this.seed = this.random.nextLong();
    }
    private int life;
    public long seed;
    private int flashes;

    public void tick() {
        if (this.life == 5 && this.level().isClientSide()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
        }

        --this.life;
        if (this.life < 0) {
            if (this.flashes == 0) {
                this.discard();
            } else {
                --this.flashes;
                this.seed = this.random.nextLong();
            }
        }

    }

    public @NotNull SoundSource getSoundSource() {
        return SoundSource.WEATHER;
    }


}
