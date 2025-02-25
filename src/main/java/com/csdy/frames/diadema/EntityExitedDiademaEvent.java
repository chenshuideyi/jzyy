package com.csdy.frames.diadema;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class EntityExitedDiademaEvent extends EntityEvent {
    public EntityExitedDiademaEvent(Entity entity, Diadema diadema) {
        super(entity);
        this.diadema = diadema;
    }

    @Getter
    private final Diadema diadema;
}
