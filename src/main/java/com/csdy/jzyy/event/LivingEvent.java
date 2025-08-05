package com.csdy.jzyy.event;

import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.item.register.HideRegister;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.csdy.jzyy.JzyyModMain.MODID;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.hasVoidWalk;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEvent {
    public LivingEvent() {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityDeath);
    }

    private void onLivingHurt(LivingHurtEvent event) {
        LivingEntity a = event.getEntity();
        Entity b = event.getSource().getEntity();
        String s = "extra_attack";
        if (b instanceof LivingEntity living && a != null) {
            if (b.getType() == JzyyEntityRegister.HJM.get()) {
                if (living.getPersistentData().getFloat(s) < 4) {
                    living.getPersistentData().putFloat(s, living.getPersistentData().getFloat(s) + 1);
                    living.playSound(JzyySoundsRegister.HAQI.get(), 1, 1);
                } else if (living.getPersistentData().getFloat(s) >= 4) {
                    living.getPersistentData().putFloat(s, 0);
                }
            }
            if (a.getType() == JzyyEntityRegister.HJM.get() && living.getType() == JzyyEntityRegister.DOG_JIAO.get()) {
                a.hurt(LegacyDamageSource.mobAttack(living), a.getMaxHealth());
            }
        }
    }

    private void onEntityDeath(LivingDeathEvent event) {
        LivingEntity a = event.getEntity();
        Entity b = event.getSource().getEntity();
        if (a.getType() == JzyyEntityRegister.HJM.get()&&b instanceof Player player&&player.level instanceof ServerLevel level){
            ItemEntity itemEntity = new ItemEntity(level, a.getX(), a.getY(), a.getZ(), HideRegister.LUE_MAO_QU.get().getDefaultInstance());
            itemEntity.setGlowingTag(true);
            itemEntity.setPickUpDelay(20);
            level.addFreshEntity(itemEntity);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.PlayerTickEvent event) {
        if (hasVoidWalk(event.player)){
            event.player.noPhysics = true;
        }
        else if (!event.player.isSpectator()){
            event.player.noPhysics = false;
        }
    }
}
