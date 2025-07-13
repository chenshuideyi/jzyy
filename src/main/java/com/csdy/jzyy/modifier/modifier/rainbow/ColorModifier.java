package com.csdy.jzyy.modifier.modifier.rainbow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ColorModifier extends NoLevelsModifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        var target = context.getLivingTarget();
        if (target != null) {
            getRainbowColorName(getEntityColor(target));
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }


    public static int getEntityColor(LivingEntity entity) {
        if (entity.level().isClientSide) {
            try {
                EntityRenderer<LivingEntity> renderer = (EntityRenderer<LivingEntity>)
                        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
                ResourceLocation texture = renderer.getTextureLocation(entity);
                System.out.println("尝试加载纹理: " + texture);

                Resource resource = Minecraft.getInstance().getResourceManager().getResource(texture).orElse(null);
                if (resource != null) {
                    try (InputStream stream = resource.open()) {
                        BufferedImage image = ImageIO.read(stream);
                        if (image != null) {
                            System.out.println("成功加载纹理: " + texture +
                                    " 尺寸: " + image.getWidth() + "x" + image.getHeight());
                            return calculateAverageColor(image);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("处理纹理时出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return 0xFFFFFF;
    }

    private static int calculateAverageColor(BufferedImage image) {
        // 采样策略：每10个像素采样一次以提高性能
        int sampleStep = 10;
        long red = 0, green = 0, blue = 0;
        int sampleCount = 0;

        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x += sampleStep) {
            for (int y = 0; y < height; y += sampleStep) {
                int rgb = image.getRGB(x, y);

                // 忽略完全透明的像素 (alpha = 0)
                if ((rgb >> 24) != 0x00) {
                    red += (rgb >> 16) & 0xFF;
                    green += (rgb >> 8) & 0xFF;
                    blue += rgb & 0xFF;
                    sampleCount++;
                }
            }
        }

        if (sampleCount == 0) {
            System.out.println("警告: 所有采样像素都是完全透明的");
            return 0xFFFFFF; // 返回白色作为默认值
        }

        int avgRed = (int) (red / sampleCount);
        int avgGreen = (int) (green / sampleCount);
        int avgBlue = (int) (blue / sampleCount);

        System.out.println("计算出的平均颜色: R=" + avgRed + " G=" + avgGreen + " B=" + avgBlue);
        return (avgRed << 16) | (avgGreen << 8) | avgBlue;
    }

    public static String getRainbowColorName(int rgb) {
        // 提取RGB分量
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // 转换为HSV/HSB颜色空间
        float[] hsv = new float[3];
        Color.RGBtoHSB(r, g, b, hsv);
        float hue = hsv[0] * 360; // 转换为0-360度

        // 定义彩虹七色的色相范围
        if (hue < 15 || hue >= 345) return "赤"; // 红色
        if (hue < 45) return "橙";     // 橙色
        if (hue < 70) return "黄";     // 黄色
        if (hue < 150) return "绿";    // 绿色
        if (hue < 195) return "青";    // 青色
        if (hue < 270) return "蓝";    // 蓝色
        return "紫";                   // 紫色
    }
}
