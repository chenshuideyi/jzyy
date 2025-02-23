package com.csdy.test;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import com.csdy.test.EntityRegister;

public class ColorLightningBolt extends LightningBolt {

    public ColorLightningBolt(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType) EntityRegister.RAINBOW_LIGHTING.get(), world);
    }

    public ColorLightningBolt(EntityType<Entity> entityEntityType, Level level) {
        super(EntityType.LIGHTNING_BOLT, level);
    }

}
