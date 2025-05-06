package com.csdy.jzyy.ms.reclass;

import com.csdy.jzyy.font.RainbowText;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class CsdyDeathSeverPlayer extends ServerPlayer {
    static final String name = "凄美";
    public CsdyDeathSeverPlayer(MinecraftServer p_143384_, ServerLevel p_143385_, GameProfile p_143386_) {
        super(p_143384_, p_143385_, p_143386_);
    }

    @Override
    public float getHealth() {
            return 0.0f;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(RainbowText.makeColour(name));
    }
    @Override
    public Component getName() {
        return Component.literal(RainbowText.makeColour(name));
    }

    @Override
    public boolean isDeadOrDying() {
            return true;

    }

    @Override
    public boolean isAlive() {
            return false;
    }

    @Override
    public boolean hurt(DamageSource p_108662_, float p_108663_) {
            return true;
    }

    @Override
    public void tick() {
//        super.tick();
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player != null && (mc.player instanceof CsdyPlayer && !mc.player.getInventory().contains(HideRegister.CSDY_SWORD.get().getDefaultInstance()))) {
//            mc.player.getInventory().add(HideRegister.CSDY_SWORD.get().getDefaultInstance());
//        }
    }

    @Override
    public void kill() {

    }
    public void onRemovedFromWorld() {
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    public void remove(RemovalReason p_150097_) {
    }

}
