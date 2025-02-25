package com.csdy.frames.diadema;


import com.csdy.frames.diadema.movement.DiademaMovement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public abstract class DiademaType {
    // instantiation
    public abstract Diadema CreateInstance(DiademaMovement movement);


    // instance&affections
    final Set<Diadema> instances = new HashSet<>();
    private final Map<Entity, Integer> entities = new HashMap<>();

    final void addAffected(Entity entity) {
        entities.replace(entity, entities.getOrDefault(entity, 0) + 1);
    }

    final void removeAffected(Entity entity) {
        var current = entities.getOrDefault(entity, 0);
        if (current > 0) {
            current -= 1;
            entities.replace(entity, current);
        }
        if (current == 0) entities.remove(entity);
    }


    // public api
    public final Set<Entity> affectedEntities = Collections.unmodifiableSet(entities.keySet());

    public boolean isAffected(Entity entity) {
        return entities.containsKey(entity);
    }

    public boolean isAffected(Vec3 pos) {
        return instances.stream().anyMatch(diadema -> diadema.getRange().ifInclude(pos));
    }
}
