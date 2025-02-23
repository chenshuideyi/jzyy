package com.csdy.until.ReClass;

import com.csdy.item.sword.CsdySword;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

public class SuperForgeGui extends ForgeGui {
    public SuperForgeGui(Minecraft mc) {
        super(mc);
    }
    @Override
    public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        if (com.csdy.until.List.GodList.isGodPlayer(minecraft.player)){
            this.minecraft.getProfiler().push("health");
            RenderSystem.enableBlend();
            Player player = (Player)this.minecraft.getCameraEntity();
            int health = Mth.ceil(20);
            boolean highlight = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
            if (health < this.lastHealth && player.invulnerableTime > 0) {
                this.lastHealthTime = Util.getMillis();
                this.healthBlinkTime = (long)(this.tickCount + 20);
            } else if (health > this.lastHealth && player.invulnerableTime > 0) {
                this.lastHealthTime = Util.getMillis();
                this.healthBlinkTime = (long)(this.tickCount + 10);
            }

            if (Util.getMillis() - this.lastHealthTime > 1000L) {
                this.lastHealth = health;
                this.displayHealth = health;
                this.lastHealthTime = Util.getMillis();
            }

            this.lastHealth = health;
            int healthLast = this.displayHealth;
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = Math.max((float)attrMaxHealth.getValue(), (float)Math.max(healthLast, health));
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

            this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
            RenderSystem.disableBlend();
            this.minecraft.getProfiler().pop();
        }

    }

    @Override
    public void renderFood(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("food");
        Player player = (Player)this.minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - this.rightHeight;
        this.rightHeight += 10;
        boolean unused = false;
        int stats = 20;
        int level = 20;

        for(int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte background = 0;
            if (this.minecraft.player.hasEffect(MobEffects.HUNGER)) {
                icon += 36;
                background = 13;
            }

            if (unused) {
                background = 1;
            }

            if (player.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (level * 3 + 1) == 0) {
                y = top + (this.random.nextInt(3) - 1);
            }

            guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 16 + background * 9, 27, 9, 9);
            if (idx < level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, icon + 36, 27, 9, 9);
            } else if (idx == level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, icon + 45, 27, 9, 9);
            }
        }

        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

    @Override
    protected void renderHUDText(int width, int height, GuiGraphics guiGraphics) {
        super.renderHUDText(width, height, guiGraphics);
    }
    protected void renderChat(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("chat");
        Window window = this.minecraft.getWindow();
        CustomizeGuiOverlayEvent.Chat event = new CustomizeGuiOverlayEvent.Chat(window, guiGraphics, this.minecraft.getFrameTime(), 0, height - 40);
        MinecraftForge.EVENT_BUS.post(event);
        guiGraphics.pose().pushPose();
        guiGraphics.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0F);
        guiGraphics.pose().translate((double)event.getPosX(), (double)(event.getPosY() - height + 40) / this.chat.getScale(), 0.0);
        int mouseX = Mth.floor(this.minecraft.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth());
        int mouseY = Mth.floor(this.minecraft.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight());
        this.chat.render(guiGraphics, this.tickCount, mouseX, mouseY);
        guiGraphics.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0F);
        guiGraphics.pose().popPose();
        this.minecraft.getProfiler().pop();
    }

    @Override
    protected void renderBossHealth(GuiGraphics guiGraphics) {
        super.renderBossHealth(guiGraphics);
    }

    @Override
    protected void renderFPSGraph(GuiGraphics guiGraphics) {
        super.renderFPSGraph(guiGraphics);
    }

    @Override
    protected void renderExperience(int x, GuiGraphics guiGraphics) {
        guiGraphics.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0F);
        RenderSystem.disableBlend();
        super.renderExperienceBar(guiGraphics,x);
        renderExperienceBar(guiGraphics, x);
        RenderSystem.enableBlend();
        guiGraphics.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0F);
    }


    public @Nullable Font getFont() {
        return ReFont.getFont();
    }


    @Override
    public void renderExperienceBar(GuiGraphics p_281906_, int p_282731_) {
        this.minecraft.getProfiler().push("expBar");
        int i = this.minecraft.player.getXpNeededForNextLevel();
        int i1;
        int j1;
            i1 = (int)(this.minecraft.player.experienceProgress * 183.0F);
            j1 = this.screenHeight - 32 + 3;
            p_281906_.blit(GUI_ICONS_LOCATION, p_282731_, j1, 0, 69, i1, 5);
            this.minecraft.getProfiler().pop();
            this.minecraft.getProfiler().push("expLevel");
            String s = "CSDY加护中";
            String s2 = "至黑之夜已开启";
            i1 = (this.screenWidth - ReFont.getFont().width(s)) -20;
            j1 = this.screenHeight - 31 - 4;
        if (com.csdy.until.List.GodList.isGodPlayer(minecraft.player)) {
            if (CsdySword.csdy)p_281906_.drawString(ReFont.getFont(), s2, i1, j1, Color.CYAN.getRGB(), false);
            p_281906_.drawString(ReFont.getFont(), s, i1, j1 - 10, Color.CYAN.getRGB(), false);
        }
        p_281906_.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0F);
        this.minecraft.getProfiler().pop();
    }

    @Override
    public ChatComponent getChat() {
        return super.getChat();
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return ReFont.getFont();
            }
        });
    }
}
