package com.csdy.jzyy.modifier.modifier.Severance;

import com.csdy.jzyy.ModMain;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.*;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.clearAbsoluteSeveranceHealth;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DeathList {
    private static final Set<String> deadEntityUuids = new HashSet<>();

    public static boolean isDeath(Entity target) {
        if (target == null) {
            return false;
        }
        return deadEntityUuids.contains(target.getStringUUID());
    }

    public static void addDeath(Entity target) {
        if (target == null) {
            return;
        }
        deadEntityUuids.add(target.getStringUUID());
    }

    public static void removeDeath(Entity target) { // Renamed for clarity, was 'remove'
        if (target == null) {
            return;
        }
        deadEntityUuids.remove(target.getStringUUID());
    }

    public static void clearDeathList() {
        deadEntityUuids.clear();
    }

    public static int getDeathListSize() {
        return deadEntityUuids.size();
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Level level = event.level;
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel) level;
                Iterator<String> iterator = deadEntityUuids.iterator();
                while (iterator.hasNext()) {
                    String uuidString = iterator.next();
                    UUID entityUuid;
                    try {
                        entityUuid = UUID.fromString(uuidString);
                    } catch (IllegalArgumentException e) {
                        iterator.remove();
                        continue;
                    }
                    Entity entity = serverLevel.getEntity(entityUuid);
                    if (entity == null) iterator.remove();
                     else if (!entity.isAlive()) iterator.remove();
                }
            }
        }
    }
}
