package com.csdy.jzyy.event;

import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;

public class LivingEvent {
    public LivingEvent(){
        MinecraftForge.EVENT_BUS.addListener(this::livinghurtevent);
    }

    private void livinghurtevent(LivingHurtEvent event) {
        LivingEntity a = event.getEntity();
        Entity b = event.getSource().getEntity();
        String s = "extra_attack";
        if (b instanceof LivingEntity living&& a !=null) {
            if(b.getType() == JzyyEntityRegister.HJM.get()) {
                if (living.getPersistentData().getFloat(s) < 3) {
                    living.getPersistentData().putFloat(s, living.getPersistentData().getFloat(s) + 1);
                    living.playSound(JzyySoundsRegister.HAQI.get(),1,1);
                }else
                if (living.getPersistentData().getFloat(s) >= 3) {
                    living.getPersistentData().putFloat(s, 0);
                }
            }
            if (a.getType() == JzyyEntityRegister.HJM.get()&&living.getType() == JzyyEntityRegister.DOG_JIAO.get()){
                a.hurt(LegacyDamageSource.mobAttack(living), a.getMaxHealth());
            }
        }
    }
}
