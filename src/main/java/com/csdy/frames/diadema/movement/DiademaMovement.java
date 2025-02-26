package com.csdy.frames.diadema.movement;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/// 司掌领域的空间变换的类，有Follow和Static两个常用的实现，也可以自己实现。
public abstract class DiademaMovement {
    public abstract Vec3 getPosition();
    public abstract ServerLevel getLevel();
}
