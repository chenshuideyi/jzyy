package com.csdy.diadema.warden;

import com.csdy.ModMain;
import com.csdy.diadema.DiademaRegister;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.EntityEnteredDiademaEvent;
import com.csdy.frames.diadema.EntityExitedDiademaEvent;
import com.csdy.frames.diadema.movement.DiademaMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 这个是重写的类型类，实例类参考名字里没Type的那个。
// 就像其他东西一样，不要直接new这个，去从注册表上拿，你应该能找到在frames包里
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WardenDiademaType extends DiademaType {
    @Override public Diadema CreateInstance(DiademaMovement movement) {
        return new WardenDiadema(this, movement);
    }

    @SubscribeEvent
    public static void onEntityEnteredDiadema(EntityEnteredDiademaEvent e) {
        if (!e.getDiadema().isOfType(DiademaRegister.WARDEN.get())) return; //检测领域类型
        if (!(e.getEntity() instanceof Player)) return; //仅影响玩家

        /// todo: 在这里进行你的操作
    }

    @SubscribeEvent
    public static void onEntityExitedDiadema(EntityExitedDiademaEvent e) {
        if (!e.getDiadema().isOfType(DiademaRegister.WARDEN.get())) return; //检测领域类型
        if (DiademaRegister.WARDEN.get().isAffected(e.getEntity())) return; //这个是为了防止玩家从多个重叠的Warden领域中的一嗝离开而被误识别为完全离开
        if (!(e.getEntity() instanceof Player)) return; //仅影响玩家

        // todo: 在这里进行你的取消操作
    }
}
