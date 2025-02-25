package com.csdy.diadema.events;

import com.csdy.diadema.Diadema;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class EntityEnteredDiademaEvent extends EntityEvent {
    public EntityEnteredDiademaEvent(Entity entity, Diadema diadema) {
        super(entity);
        this.diadema = diadema;
    }

    @Getter
    private final Diadema diadema;
}

