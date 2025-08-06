package com.csdy.jzyy.modifier.util.layer;

import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
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

@OnlyIn(Dist.CLIENT)
public class PlayerCreeperArmorLayer extends RenderLayer {
    //雷凯
    private static final ResourceLocation CREEPER_ARMOR_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final HumanoidModel<Player> model;
    private final PlayerRenderer render;

    public PlayerCreeperArmorLayer(PlayerRenderer renderer) {
        super(renderer);
        Minecraft mc = Minecraft.getInstance();
        EntityRendererProvider.Context context = new EntityRendererProvider.Context(
                mc.getEntityRenderDispatcher(),mc.getItemRenderer(),mc.blockRenderer,mc.gameRenderer.itemInHandRenderer, mc.getResourceManager(),mc.getEntityModels(),mc.font
        );
        this.model = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        this.render=renderer;
    }



    protected float xOffset(float xOffset) {
        return xOffset * 0.01F;
    }

    protected ResourceLocation getTextureLocation() {
        return CREEPER_ARMOR_LOCATION;
    }

    protected EntityModel<Player> model() {
        return this.model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Entity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(entity instanceof Player player)) return;
        if (!player.hasEffect(JzyyEffectRegister.OVERCHARGE_ARMOR.get())) return;

        // 1. 在盔甲层之后渲染
        poseStack.pushPose();

        // 2. 稍微放大模型使其显示在盔甲外面
        float scale = 1.3f; // 2%放大
        poseStack.scale(scale, scale, scale);

        // 3. 调整渲染参数
        float time = (float)player.tickCount + partialTicks;
        VertexConsumer vertexConsumer = buffer.getBuffer(
                RenderType.energySwirl(
                        CREEPER_ARMOR_LOCATION,
                        this.xOffset(time) % 1.0F,
                        time * 0.01F % 1.0F
                )
        );

        // 4. 设置模型动画并渲染
        this.model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0.5F, 0.5F, 1.0F, 1.0F // 调整颜色和透明度
        );

        poseStack.popPose();
    }
}
