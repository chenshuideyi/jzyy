package com.csdy.diadema.rule;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RuleDiadema {

//    @SubscribeEvent
//    public void Rule(LivingDamageEvent e) {
//        LivingEntity living = e.getEntity();
//        if (this.affectingEntities.contains(living)) {
//            System.out.println("吃了一个");
//            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
//            double originalMaxHealth = maxHealthAttr.getBaseValue();
//            double reducedMaxHealth = originalMaxHealth + living.getMaxHealth()*0.8;
//            maxHealthAttr.setBaseValue(reducedMaxHealth);
//
//            AttributeInstance AttackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
//            double originalAttack = AttackAttr.getBaseValue();
//            double reducedAttack = originalAttack + living.getAttributes().getBaseValue(Attributes.ATTACK_DAMAGE)*0.8;
//            AttackAttr.setBaseValue(reducedAttack);
//
//            player.heal(10);
//            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+5);
//            player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+5);
//        }
//    }
//
}
