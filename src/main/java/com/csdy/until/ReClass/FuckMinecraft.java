package com.csdy.until.ReClass;

import com.csdyms.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class FuckMinecraft extends Minecraft {
    public FuckMinecraft(GameConfig p_91084_) {
        super(p_91084_);
    }

    @Override
    public void run() {
        if (this.player != null) {
//            if (DeadLists.isEntity(this.player)) {
                ForgeGui forgeGui = new ForgeGui(Minecraft.getInstance());
                Helper.replaceclass(forgeGui, CsdyForgeGui.class);
//            }
            this.window.setTitle("Minecraft 1.20.1 Health:  " + Minecraft.getInstance().player.getHealth() + "  maxHealth:  " + Minecraft.getInstance().player.getMaxHealth() +"  FPS:  " +fps);
        } else {
            this.window.setTitle("Minecraft 1.20.1 FPS: " + fps);
        }
//        Helper.replaceclass(MinecraftForge.EVENT_BUS, FuckEventBus.class);
        super.run();
    }

    @Override
    public void updateTitle() {
        if (this.player != null) {
            this.window.setTitle("Minecraft Health:  " + Minecraft.getInstance().player.getHealth() + "  maxHealth:  " + Minecraft.getInstance().player.getMaxHealth() +"  FPS:  " +fps);
        } else {
            this.window.setTitle("Minecraft FPS: " + fps);
        }
    }





}
