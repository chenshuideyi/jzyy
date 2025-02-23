package com.csdy.until.ReClass;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CsdyForgeGui extends ForgeGui {
    public CsdyForgeGui(Minecraft mc) {
        super(mc);
    }

    public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("health");
        RenderSystem.enableBlend();
        Player player = (Player)this.minecraft.getCameraEntity();

        boolean highlight = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        if (0 < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long)(this.tickCount + 20);
        } else if (0 > this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long)(this.tickCount + 10);
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L) {

            this.lastHealth = 0;
            this.displayHealth = 0;
            this.lastHealthTime = Util.getMillis();

        }

        this.lastHealth = 0;
        int healthLast = this.displayHealth;
        int attrMaxHealth = 0 ;
        float healthMax = 0;
        int absorb = Mth.ceil(player.getAbsorptionAmount());
        int healthRows = Mth.ceil((healthMax + (float)absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        this.random.setSeed((long)(this.tickCount * 312871));
        int left = width / 2 - 91;
        int top = height - this.leftHeight;
        this.leftHeight += healthRows * rowHeight;
        if (rowHeight != 10) {
            this.leftHeight += 10 - rowHeight;
        }
        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
        }
        this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, 0, healthLast, absorb, highlight);
        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

}
