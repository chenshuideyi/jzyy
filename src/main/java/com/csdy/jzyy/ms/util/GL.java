package com.csdy.jzyy.ms.util;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.client.ForgeHooksClient;


public class GL {

    /// by Plzlizi
    public static void glDeath(Minecraft mc) {
        while (mc.running) {
            try {

                SoundPlayer.tryPlayMillenniumSnowAsync("hated_by_life_itself.wav");
                if (mc.window.shouldClose()) {
                    mc.stop();
                }
                mc.resizeDisplay();
                draw(mc);
                mc.window.updateDisplay();
                draw(mc);
                Thread.yield();


            } catch (Exception ignored) {
            }
        }
    }

    private static float getRand(int x, int y) {
        Random random = new Random();
        return random.nextFloat(y - x + 1) + x;
    }

    private static void draw(Minecraft mc) {
        try {
            LocalPlayer old = Objects.requireNonNull(mc.player);
            mc.player = new LocalPlayer(old.minecraft, old.clientLevel, old.connection, old.stats, old.recipeBook, false, false) {
                @Override
                public float getHealth() {
                    return 0.0F;
                }

                @Override
                public FoodData getFoodData() {
                    return new FoodData() {
                        @Override
                        public int getFoodLevel() {
                            return 0;
                        }
                    };
                }
            };
            mc.cameraEntity = mc.player;

            int width = mc.window.getGuiScaledWidth();
            int height = mc.window.getGuiScaledHeight();

            Window window = mc.getWindow();
            RenderSystem.clear(256, Minecraft.ON_OSX);
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0F, 0.0F, ForgeHooksClient.getGuiFarPlane());
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.setIdentity();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();

            GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers.bufferSource());// 如果想绕过大部分拦截用自己的类
            graphics.blit(new ResourceLocation("jzyy", "textures/gui/death.png"), 0, 0, 0, 0.0F, 0.0F, width, height, width, height);
            //这里换绘制的死亡gui
            try {
                // 1. 处理tick方法（优先tick，失败后m_93066_）
                Method m = tryGetMethod(Gui.class, "tick", "m_93066_");
                m.setAccessible(true);
                m.invoke(mc.gui);

                // 2. 处理render方法（优先render，失败后m_280421_）
                m = tryGetMethod(Gui.class, "render", "m_280421_", GuiGraphics.class, float.class);
                m.setAccessible(true);
                m.invoke(mc.gui, graphics, 1.0F);
            } catch (Exception ignored) {

            }

            graphics.pose().pushPose();
            try {
                graphics.fillGradient(0, 0, width, height, 0xFF000000, 0xFF000000);
//                graphics.fillGradient(0, 0, width, height, new Random().nextInt() * 10, new Random().nextInt() * 10);
            } catch (Exception ignored) {
            }
            graphics.pose().popPose();

            graphics.pose().pushPose();
            graphics.pose().scale(3.0F, 3.0F, 3.0F);
//            graphics.drawCenteredString(mc.font, Component.literal("- D E A T H -"), width / 2 / 3 + (int) getRand(-5, 5), 18 + (int) getRand(-5, 5), new Random().nextInt() * 10);// + getRand(-20, 20)
            graphics.drawCenteredString(
                    mc.font,
                    Component.literal("你死了"),
                    width / 2 / 3,
                    18,
                    0xFFFFFFFF // 纯白色（ARGB格式）
            );
            graphics.pose().popPose();

            graphics.pose().pushPose();
//			Button button = Button.builder(Component.translatable("deathScreen.respawn"), (p_280796_) -> {
//			}).bounds(width / 2 - 100, height / 4 + 72, 200, 20).build();

//			graphics.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
//			graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
//			graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            ///后续可以优化一下

            Component component = Component.literal("重生");
//            Component component = Component.literal(RainbowText.makeColour("重生"));
//            Component component = Component.nullToEmpty(makeColour("deathScreen.respawn"));

            Button button = Button.builder(component, (btn) -> {
                    })
                    .bounds(width / 2 - 100, height / 4 + 72, 200, 20)
                    .build();


//            Component component1 = Component.literal(RainbowText.makeColour("你确定要退出吗？"));
            Component component1 = Component.literal("你确定要退出吗？");
//            Component component1 = Component.nullToEmpty(makeColour("deathScreen.quit.confirm"));
            Button button1 = Button.builder(component1, (btn) -> {
                    })
                    .bounds(width / 2 - 100, height / 4 + 96, 200, 20)
                    .build();

//			graphics.setColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
//			graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
//			graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().popPose();

            button1.render(graphics, 0, 0, 0.0F);
            button.render(graphics, 0, 0, 0.0F);

            mc.getToasts().render(graphics);
            graphics.flush();
            mc.getProfiler().push("toasts");
            mc.getToasts().render(graphics);
            mc.getProfiler().pop();
            graphics.flush();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        } catch (Exception ignored) {
        }
    }

    private static Method tryGetMethod(Class<?> clazz, String primaryName, String fallbackName, Class<?>... paramTypes)
            throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(primaryName, paramTypes);
        } catch (NoSuchMethodException e) {
            return clazz.getDeclaredMethod(fallbackName, paramTypes);
        }
    }

}
