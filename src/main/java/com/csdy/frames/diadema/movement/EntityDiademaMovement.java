package com.csdy.frames.diadema.movement;

import lombok.Getter;
import net.minecraft.world.entity.Entity;

// 有核心实体的movement，使用该类型movement时可从Diadema上获取core或player
@SuppressWarnings("LombokGetterMayBeUsed")
public abstract class EntityDiademaMovement extends DiademaMovement {
    protected EntityDiademaMovement(Entity entity) {
        this.entity = entity;
    }

    @Getter
    protected final Entity entity;
}
