package com.csdy.effect;

import com.csdy.ModMain;
import com.csdy.effect.register.EffectRegister;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Wind extends MobEffect {
    private static final Random random = new Random();

    private static final String SPEED_MODIFIER_UUID = "7107DE5E-7CE8-4030-940E-514C1F160890"; // 唯一标识符
    private static final double SPEED_REDUCTION = 3;

    public Wind() {
        super(MobEffectCategory.HARMFUL, 0);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID, SPEED_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(EffectRegister.PHYSICALINJURY.get())) {
            double chance = random.nextDouble();
            // 如果随机数小于 0.8（80% 概率），取消伤害
            if (chance < 0.8) event.setCanceled(true);

        }
    }
}
