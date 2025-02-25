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

public abstract class Diadema {
    // init&final
    public Diadema(DiademaRange range, DiademaMovement movement) {
        this.range = range;
        this.movement = movement;

        // 实例上的事件处理器需要手动注册
        MinecraftForge.EVENT_BUS.register(this);
        //FMLJavaModLoadingContext.get().getModEventBus().register(this); //不是这个bus，注释掉了
    }

    private void remove() {
        // 视作实体离开领域
        for (Entity entity : entities) {
            entities.remove(entity);
            MinecraftForge.EVENT_BUS.post(new EntityExitedDiademaEvent(entity, this));
        }

        // 同样实例在删除时候需要手动取消注册。不然因为注册也是个引用，可能导致内存泄漏或者null引用异常
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    // properties
    @Getter @Setter
    private Level level;

    @Getter @Setter
    private Vec3 position;

    public void setPosition(double x, double y, double z) {
        position = new Vec3(x, y, z);
    }

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

    private final HashSet<Entity> entities = new HashSet<>();

    public final Set<Entity> affectingEntities = Collections.unmodifiableSet(entities);


    // virtual methods
    protected void perTick() {
    }


    // state updating
    private void updateEntities() {
        var inRange = range.getAffectingEntities(level, position);
        entities.stream().filter(entity -> !inRange.contains(entity)).toList().forEach(entity -> {
            entities.remove(entity);
            MinecraftForge.EVENT_BUS.post(new EntityExitedDiademaEvent(entity, this));
        });
        for (Entity entity : inRange) {
            if (entities.add(entity))
                MinecraftForge.EVENT_BUS.post(new EntityEnteredDiademaEvent(entity, this));
        }
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
