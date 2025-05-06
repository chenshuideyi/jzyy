package com.csdy.jzyy.ms;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.clearAbsoluteSeveranceHealth;

//public class CoreUtil {
//
//    private static final ConcurrentMap<UUID, Float> CSDY_KILL_MAP = new ConcurrentHashMap<>();
//
//    public static void setCsdyKillMap(LivingEntity entity, float expectedHealth) {
//        if (entity == null) return;
//        UUID uuid = entity.getUUID();
//        CSDY_KILL_MAP.put(uuid, expectedHealth);
//    }
//
//    public static float getCsdyKillMap(LivingEntity entity) {
//        if (entity == null) return Float.NaN;
//        return CSDY_KILL_MAP.getOrDefault(entity.getUUID(), Float.NaN);
//    }
//
//    public static void clearCsdyKillMap(UUID uuid) {
//        if (uuid != null) {
//            CSDY_KILL_MAP.remove(uuid);
//        }
//    }
//
//    public static ConcurrentMap<UUID, Float> getCsdyKillMap() {
//        return CSDY_KILL_MAP;
//    }
//
//    @SubscribeEvent
//    public static void onLevelTick(TickEvent.LevelTickEvent event) {
//        if (event.phase != TickEvent.Phase.END) return;
//        if (!(event.level instanceof ServerLevel serverLevel)) return;
//        for (UUID uuid : getCsdyKillMap().keySet()) {
//            Entity entity = serverLevel.getEntity(uuid);
//            if (entity instanceof LivingEntity living) {
//
//            } else {
//                clearCsdyKillMap(uuid);
//            }
//        }
//    }
//
//}
