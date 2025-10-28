package com.csdy.jzyy.modifier.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class CuriosUtil {

    public static void removeAllCurios(LivingEntity player) {
        Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve();
        if (curiosHandler.isPresent()) {
            ICuriosItemHandler handler = (ICuriosItemHandler) curiosHandler.get();
            // 移除了filter，现在会处理所有饰品槽位
            handler.getCurios().forEach((key, value) -> {
                int slots = value.getSlots();
                for (int i = 0; i < slots; ++i) {
                    value.getStacks().setStackInSlot(i, ItemStack.EMPTY);
                }
            });
        }
    }

}
