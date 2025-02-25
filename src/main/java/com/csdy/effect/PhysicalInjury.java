package com.csdy.effect;

import com.csdy.ModMain;
import com.csdy.effect.register.EffectRegister;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhysicalInjury extends MobEffect {
    public PhysicalInjury() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(EffectRegister.PHYSICALINJURY.get())) {
            entity.setHealth(1.0F); // 受到伤害时将生命值设置为 1
            entity.removeEffect(EffectRegister.PHYSICALINJURY.get());
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(EffectRegister.PHYSICALINJURY.get())) {
            entity.setHealth(1.0F); // 受到治疗时将生命值设置为 1
            entity.removeEffect(EffectRegister.PHYSICALINJURY.get());
        }
    }
}
