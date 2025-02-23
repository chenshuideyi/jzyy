package com.csdyms;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ReThread {
    public static void Thread(LivingEntity entity){
        Object lock = new Object();
        (new Thread(() ->{
//            while (Minecraft.getInstance().isRunning()){
                synchronized (lock){
                    try{
                        lock.wait(50);
                    } catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }
//            }
        })).start();
    }
}
