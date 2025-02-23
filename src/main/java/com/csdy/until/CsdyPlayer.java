package com.csdy.until;

import com.csdy.item.register.HideRegister;
import com.csdy.until.List.DeadLists;
import com.csdy.until.List.GodList;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class CsdyPlayer extends LocalPlayer {
    static final String name = "Paradox-Ishmerai-Netra-Cogito-Hinanawi-Kirisame-Veni,Vidi,Vici-Atramentum-Veritas-Csdy";
    public CsdyPlayer(Minecraft p_108621_, ClientLevel p_108622_, ClientPacketListener p_108623_,
                       StatsCounter p_108624_, ClientRecipeBook p_108625_, boolean p_108626_, boolean p_108627_) {
        super(p_108621_, p_108622_, p_108623_, p_108624_, p_108625_, p_108626_, p_108627_);

    }

    @Override
    public float getHealth() {
            return 20.0f;
    }

    @Override
    public Component getDisplayName() {
            return Component.literal(RainbowText.setColour(name));
    }
    @Override
    public Component getName() {
            return Component.literal(RainbowText.setColour(name));
    }

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


