package com.csdy.modifier.method;

import com.csdy.ModMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class SpeedMethod {
    public static void setWalkingSpeed(Player player, double speed) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            // 定义一个唯一的 UUID
            UUID modifierId = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890"); // 替换为你自己的 UUID
            // 移除旧的修饰符
            movementSpeed
                    .removeModifier(modifierId);
            // 添加新的修饰符
            movementSpeed
                    .addPermanentModifier(new AttributeModifier(
                            modifierId
                            , // 使用 UUID
                            "diadema_walking_speed", // 修饰符名称
                            speed
                                    - movementSpeed.getBaseValue(), // 计算差值
                            AttributeModifier.Operation.ADDITION // 使用加法操作
                    ));
        }
    }
    public static void setFlyingSpeed(Player player, float speed) {
        Abilities abilities = player.getAbilities();
        abilities.setFlyingSpeed(speed); // 设置飞行速度
        player.onUpdateAbilities(); // 更新玩家的能力
    }
}
