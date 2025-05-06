package com.csdy.jzyy.ms.reclass;

import com.csdy.jzyy.font.RainbowText;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.damagesource.DamageSource;

public class CsdyDeathPlayer extends LocalPlayer {
    static final String name = "凄美";
    public CsdyDeathPlayer(Minecraft p_108621_, ClientLevel p_108622_, ClientPacketListener p_108623_,
                           StatsCounter p_108624_, ClientRecipeBook p_108625_, boolean p_108626_, boolean p_108627_) {
        super(p_108621_, p_108622_, p_108623_, p_108624_, p_108625_, p_108626_, p_108627_);

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
        super.tick();
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player != null && (mc.player instanceof CsdyPlayer && !mc.player.getInventory().contains(HideRegister.CSDY_SWORD.get().getDefaultInstance()))) {
//            ItemHandlerHelper.giveItemToPlayer(mc.player, new ItemStack(HideRegister.CSDY_SWORD.get()));
//            mc.player.getInventory().add(HideRegister.CSDY_SWORD.get().getDefaultInstance());
//        }
    }

    @Override
    public void kill() {

    }


}


