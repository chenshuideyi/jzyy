package com.csdy.frames.diadema;


import com.csdy.frames.diadema.movement.DiademaMovement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/// 领域类型的抽象基类，实现后把获取实例的方法注册在注册表上<br/>
/// 值得一提的事，因为注册的是单个实例所以一个class也能用作多个实际的type<br/>
/// 也就是说要用时候就像Block或者MobType一样得从注册表上获取而不是new 一个
public abstract class DiademaType {
    // instantiation
    /// 这个方法用以创建一个领域的实例，实例化时要填入移动方式，我们有内置的绑定实体和静态两种，也可以自己实现
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
    /// 所有受这一类型的(即从这个对象的CreateInstance方法创建的)领域影响的生物的视图，注意是只读的，进行更改会抛出异常
    public final Set<Entity> affectedEntities = Collections.unmodifiableSet(entities.keySet());

    /// 用于判断实体是否被这一类型的任意一个领域所影响
    public boolean isAffected(Entity entity) {
        return entities.containsKey(entity);
    }

    /// 用于判断方块是否在这一类型的任意一个领域的范围内
    public boolean isAffected(Vec3 pos) {
        return instances.stream().anyMatch(diadema -> diadema.getRange().ifInclude(pos));
    }
}
