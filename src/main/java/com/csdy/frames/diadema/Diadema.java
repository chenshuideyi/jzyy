package com.csdy.frames.diadema;

import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Diadema {
    // init&final
    public Diadema(DiademaType type, DiademaRange range, DiademaMovement movement) {
        this.type = type;
        this.range = range;
        this.movement = movement;

        // 实例上的事件处理器需要手动注册
        MinecraftForge.EVENT_BUS.register(this);
        //FMLJavaModLoadingContext.get().getModEventBus().register(this); //不是这个bus，注释掉了

        // 加到type上的列表里
        type.instances.add(this);
    }

    private void remove() {
        // 视作实体离开领域
        entities.forEach(this::removeEntity);

        // 同样实例在删除时候需要手动取消注册。不然因为注册也是个引用，可能导致内存泄漏或者null引用异常
        MinecraftForge.EVENT_BUS.unregister(this);

        // 从type上的列表里移除
        type.instances.remove(this);
    }


    // properties&methods
    @Getter
    private final DiademaType type;

    public boolean isOfType(DiademaType type) {
        return this.type == type;
    }

    @Getter
    private Level level;

    @Getter
    private Vec3 position;

    public double getPosX() {
        return position.x;
    }

    public double getPosY() {
        return position.y;
    }

    public double getPosZ() {
        return position.z;
    }

    @Getter
    private final DiademaRange range;

    private final DiademaMovement movement;

    private final Set<Entity> entities = new HashSet<>();

    public final Set<Entity> affectingEntities = Collections.unmodifiableSet(entities);

    public void kill() {
        remove();
    }


    // virtual methods
    protected void perTick() {
    }


    // state updating
    private void removeEntity(Entity entity) {
        if (entities.remove(entity)) {
            type.removeAffected(entity);
            MinecraftForge.EVENT_BUS.post(new EntityExitedDiademaEvent(entity, this));
        }
    }

    private void addEntity(Entity entity) {
        if (entities.add(entity)) {
            type.addAffected(entity);
            MinecraftForge.EVENT_BUS.post(new EntityEnteredDiademaEvent(entity, this));
        }
    }

    private void updateEntities() {
        var inRange = range.getAffectingEntities();
        entities.stream().filter(entity -> !inRange.contains(entity)).toList().forEach(this::removeEntity);
        inRange.forEach(this::addEntity);
    }

    private void updatePosition() {
        position = movement.getPosition();
        level = movement.getLevel();
    }


    // event handlers
    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent e) {
        updateEntities();
        updatePosition();
        perTick();
    }
}
