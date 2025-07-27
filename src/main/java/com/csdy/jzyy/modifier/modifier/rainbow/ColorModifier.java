package com.csdy.jzyy.modifier.modifier.rainbow;

import com.csdy.jzyy.JzyyModMain;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public class ColorModifier extends NoLevelsModifier implements MeleeHitModifierHook, TooltipModifierHook, MeleeDamageModifierHook {

    ///Tag具体类型
    ///@see net.minecraft.nbt.Tag

    private static final ResourceLocation ENTITY_COLOR_RED = new ResourceLocation(JzyyModMain.MODID, "entity_color_red");
    private static final ResourceLocation ENTITY_COLOR_GREEN = new ResourceLocation(JzyyModMain.MODID, "entity_color_green");
    private static final ResourceLocation ENTITY_COLOR_BLUE = new ResourceLocation(JzyyModMain.MODID, "entity_color_blue");

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float v, float v1) {
        LivingEntity target = context.getLivingTarget();
        if (target != null) {
            ModDataNBT data = tool.getPersistentData();
            int[] rgb = getEntityColor(target);

            // 存储RGB分量
            data.putInt(ENTITY_COLOR_RED, rgb[0]);
            data.putInt(ENTITY_COLOR_GREEN, rgb[1]);
            data.putInt(ENTITY_COLOR_BLUE, rgb[2]);

        }
        return 0;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        ModDataNBT data = tool.getPersistentData();

            // 获取RGB分量
            int red = data.getInt(ENTITY_COLOR_RED);
            int green = data.getInt(ENTITY_COLOR_GREEN);
            int blue = data.getInt(ENTITY_COLOR_BLUE);

            // 创建带颜色的文本组件
            Component redText = Component.literal(String.valueOf(red))
                    .withStyle(style -> style.withColor(ChatFormatting.RED));
            Component greenText = Component.literal(String.valueOf(green))
                    .withStyle(style -> style.withColor(ChatFormatting.GREEN));
            Component blueText = Component.literal(String.valueOf(blue))
                    .withStyle(style -> style.withColor(ChatFormatting.BLUE));
            // 添加RGB显示
            tooltip.add(Component.translatable("tooltip.color_enchant.rgb_values")
                    .append(redText)
                    .append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
                    .append(greenText)
                    .append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
                    .append(blueText)
                    .withStyle(ChatFormatting.GRAY));


    }

    public static int[] getEntityColor(LivingEntity entity) {
        if (entity instanceof EnderDragon) return new int[]{0, 0, 0};

        // 确保只在客户端计算
//        if (!entity.level().isClientSide) {
//            return new int[]{0, 0, 0};
//        }

        try {
            EntityRenderer<LivingEntity> renderer = (EntityRenderer<LivingEntity>)
                    Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
            ResourceLocation texture = renderer.getTextureLocation(entity);
            System.out.println("正在加载实体纹理: " + texture);

            try (InputStream stream = Minecraft.getInstance().getResourceManager()
                    .getResource(texture).orElseThrow().open()) {
                BufferedImage image = ImageIO.read(stream);
                if (image != null) {
                    return calculateAverageColor(image);
                }
            }
        } catch (Exception e) {
            System.out.println("纹理加载失败: " + e.getMessage());
        }
        return new int[]{0, 0, 0};
    }

    private static int[] calculateAverageColor(BufferedImage image) {
        // 采样步长 - 可根据性能需求调整
        final int sampleStep = 10;
        long redSum = 0, greenSum = 0, blueSum = 0;
        int validPixels = 0;

        // 获取图像边界
        final int width = image.getWidth();
        final int height = image.getHeight();

        // 采样像素
        for (int y = 0; y < height; y += sampleStep) {
            for (int x = 0; x < width; x += sampleStep) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;

                // 只处理不透明像素
                if (alpha > 0) {
                    redSum += (argb >> 16) & 0xFF;
                    greenSum += (argb >> 8) & 0xFF;
                    blueSum += argb & 0xFF;
                    validPixels++;
                }
            }
        }

        // 处理全透明图像的情况
        if (validPixels == 0) {
            System.out.println("警告: 未找到有效像素，返回白色");
            return new int[]{255, 255, 255};
        }

        // 计算平均值
        int avgRed = (int)(redSum / validPixels);
        int avgGreen = (int)(greenSum / validPixels);
        int avgBlue = (int)(blueSum / validPixels);

        System.out.printf("平均颜色计算结果: R=%d G=%d B=%d%n", avgRed, avgGreen, avgBlue);
        return new int[]{avgRed, avgGreen, avgBlue};
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
    }

//    @Override
//    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
//        LivingEntity target = context.getLivingTarget();
//        if (target != null) {
//            ModDataNBT data = tool.getPersistentData();
//            int colorValue = getEntityColor(target);
//            String colorName = getRainbowColorName(colorValue);
//
//            // 将颜色数据存入NBT
//            data.putInt(ENTITY_COLOR, colorValue);
//            data.putString(ResourceLocation.tryParse(ENTITY_COLOR + "_name"), colorName);
//        }
//    }

}
