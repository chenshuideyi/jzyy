package com.csdy.jzyy.ms.reclass;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class CsdyServerPlayer extends ServerPlayer {
    static final String name = "Paradox-Ishmerai-Netra-Cogito-Hinanawi-Kirisame-Veni,Vidi,Vici-Atramentum-Veritas-Csdy";
    public CsdyServerPlayer(MinecraftServer p_143384_, ServerLevel p_143385_, GameProfile p_143386_) {
        super(p_143384_, p_143385_, p_143386_);
    }

    @Override
    public float getHealth() {
            return 20.0f;
    }

//    @Override
//    public Component getDisplayName() {
//            return Component.literal(RainbowText.setColour(name));
//    }
//    @Override
//    public Component getName() {
//            return Component.literal(RainbowText.setColour(name));
//    }

    @Override
    public boolean isDeadOrDying() {
            return false;

    }

    @Override
    public boolean isAlive() {
            return true;
    }

    @Override
    public boolean hurt(DamageSource p_108662_, float p_108663_) {
            return false;
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
