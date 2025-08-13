package com.csdy.jzyy.modifier.util.layer;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PlayerCreeperArmorLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final PlayerModel<AbstractClientPlayer> model;
    private final PlayerRenderer renderer;

    public PlayerCreeperArmorLayer(PlayerRenderer renderer) {
        super(renderer);
        this.renderer = renderer;
        Minecraft mc = Minecraft.getInstance();
        EntityRendererProvider.Context context = new EntityRendererProvider.Context(
                mc.getEntityRenderDispatcher(),
                mc.getItemRenderer(),
                mc.getBlockRenderer(),
                mc.getEntityRenderDispatcher().getItemInHandRenderer(),
                mc.getResourceManager(),
                mc.getEntityModels(),
                mc.font
        );
        this.model = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.model.setAllVisible(true);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.hasEffect(JzyyEffectRegister.OVERCHARGE_ARMOR.get())) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) return;

        poseStack.pushPose();

        try {
            // 关键修改1：移除translate调整，让模型自然跟随玩家
            poseStack.translate(0.0D, -1.301D, 0.0D);

            // 调整缩放比例，稍微放大以包裹玩家
            float scale = 1.82f; // 比玩家稍大
            poseStack.scale(scale, scale, scale);

            float time = player.tickCount + partialTicks;

            // 关键修改2：使用能量漩涡渲染类型
            VertexConsumer vertexConsumer = buffer.getBuffer(
                    RenderType.energySwirl(
                            LIGHTNING_TEXTURE,
                            this.getLightningXOffset(time) % 1.0F,
                            time * 0.03F % 1.0F
                    )
            );

            // 关键修改3：正确复制模型属性
            this.model.copyPropertiesTo(this.getParentModel()); // 注意是copyPropertiesFrom
            this.model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            // 渲染主闪电层
            this.model.renderToBuffer(
                    poseStack,
                    vertexConsumer,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    0.2F, 0.5F, 1.0F, 0.7F // 蓝白色闪电
            );

            // 添加第二层增强效果
            VertexConsumer secondPass = buffer.getBuffer(
                    RenderType.energySwirl(
                            LIGHTNING_TEXTURE,
                            this.getLightningXOffset(time * 1.5F) % 1.0F,
                            time * 0.02F % 1.0F
                    )
            );

            this.model.renderToBuffer(
                    poseStack,
                    secondPass,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    0.7F, 0.8F, 1.0F, 0.5F // 更亮的第二层
            );
        } finally {
            poseStack.popPose();
        }
    }

    private float getLightningXOffset(float time) {
        return time * 0.02F;
    }
}
