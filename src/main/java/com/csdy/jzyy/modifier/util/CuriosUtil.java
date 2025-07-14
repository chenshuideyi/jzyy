package com.csdy.jzyy.modifier.util;

public class CuriosUtil {

//    @Deprecated
//    public static boolean hasCurios(LivingEntity player, Item item) {
//        if (!(player instanceof Player)) {
//            return false;
//        } else {
//            Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve();
//            if (curiosHandler.isPresent()) {
//                ICuriosItemHandler handler = curiosHandler.get();
//                return handler.getCurios().entrySet().stream().filter((entry) -> {
//                    return entry.getKey().contains("blue_archivescraft_");
//                }).anyMatch((entry) -> {
//                    int slots = entry.getValue().getSlots();
//
//                    for (int i = 0; i < slots; ++i) {
//                        if (entry.getValue().getStacks().getStackInSlot(i).getItem() == item) {
//                            return true;
//                        }
//                    }
//
//                    return false;
//                });
//            } else {
//                return false;
//            }
//        }
//    }
//
//    public static void removeCurios(LivingEntity player, Item item) {
//        Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve();
//        if (curiosHandler.isPresent()) {
//            ICuriosItemHandler handler = (ICuriosItemHandler) curiosHandler.get();
//            handler.getCurios().entrySet().stream().filter((entry) -> {
//                return ((String) entry.getKey()).contains("blue_archivescraft_");
//            }).forEach((entry) -> {
//                int slots = entry.getValue().getSlots();
//
//                for (int i = 0; i < slots; ++i) {
//                    if (entry.getValue().getStacks().getStackInSlot(i).getItem() == item) {
//                        entry.getValue().getStacks().getStackInSlot(i).setCount(0);
//                    }
//                }
//
//            });
//        }
//    }
}
