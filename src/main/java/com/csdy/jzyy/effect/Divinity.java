package com.csdy.jzyy.effect;

import com.csdy.jzyy.JzyyModMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Divinity extends MobEffect {

    private static final String ATTACK_SPEED_MODIFIER_UUID = "7107DE5E-7CE8-4030-940E-514C1F160890"; // 唯一标识符
    private static final double ATTACK_SPEED_REDUCTION = 2;

    public Divinity() {
        super(MobEffectCategory.BENEFICIAL, 0);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_UUID, ATTACK_SPEED_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }



    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        entity.setHealth(0);
    }

    @SubscribeEvent
    public static void KamuiPlus(LivingAttackEvent event) {
        //双神威
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(JzyyEffectRegister.DIVINITY.get())) {
                player.invulnerableTime = 1;
                event.setCanceled(true);
            }

        }
    }

    @SubscribeEvent
    public static void FuckYouDraconicEvolution(LivingDeathEvent event) {
        //双神威免死 操他妈的混沌守卫
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(JzyyEffectRegister.DIVINITY.get())) {
                player.setHealth(player.getMaxHealth());
                player.deathTime = -2;
                player.isAlive();
                event.setCanceled(true);
            }

        }
    }

    @SubscribeEvent
    public static void Kamuidontdie(TickEvent.PlayerTickEvent event) {
        //双神威免死plus
        Player player = event.player;
        if (player.hasEffect(JzyyEffectRegister.DIVINITY.get())) {
            player.deathTime = 0;
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof DeathScreen) {
                mc.screen = null;
                mc.setScreen(null);
            }
            player.setHealth(player.getMaxHealth());
        }

    }
}
