package com.csdy.jzyy.modifier.util.layer;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.effect.JzyyEffectRegister;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import static com.csdy.jzyy.modifier.util.layer.TestRender.renderCircle;
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HaloRenderLayer extends RenderLayer{
    // 光环模型 - 一个简单的圆环
    private final HumanoidModel<Player> haloModel;

    public HaloRenderLayer(RenderLayerParent renderer) {
        super(renderer);
        Minecraft mc = Minecraft.getInstance();
        // 创建光环模型
        EntityRendererProvider.Context context = new EntityRendererProvider.Context(
                mc.getEntityRenderDispatcher(),mc.getItemRenderer(),mc.blockRenderer,mc.gameRenderer.itemInHandRenderer, mc.getResourceManager(),mc.getEntityModels(),mc.font
        );
        this.haloModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
    }



    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight,
                       Entity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {


        if (!(entity instanceof Player player)) return;
        if (!player.hasEffect(JzyyEffectRegister.OVERCHARGE_ARMOR.get())) return;
        renderCircle(stack,buffer,player,limbSwing,packedLight,partialTicks,ageInTicks,netHeadYaw,headPitch);
//        // 检查是否应该渲染光环（可以根据条件判断）
//        if (!shouldRenderHalo((Player) player)) {
//            return;
//        }

//        poseStack.pushPose();
//
//        // 调整位置到玩家脚底
//        poseStack.translate(0, -1.6, 0); // 调整Y值使光环在脚底
//
//        // 使光环始终水平
//        poseStack.mulPose(Axis.XP.rotationDegrees(90));
//
//        // 旋转光环（可选）
//        float rotation = (player.tickCount + partialTicks) * 2; // 缓慢旋转
//        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
//
//        // 设置颜色和透明度
//        int color = 0xFF00FF; // 紫色，可以自定义
//        float alpha = 0.7f; // 透明度
//        float red = ((color >> 16) & 0xFF) / 255f;
//        float green = ((color >> 8) & 0xFF) / 255f;
//        float blue = (color & 0xFF) / 255f;
//
//        // 渲染光环
//        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityTranslucentCull((ResourceLocation) RenderType.ENTITY_SHADOW));
//        this.haloModel.renderToBuffer(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
//
//        poseStack.popPose();
    }

    private boolean shouldRenderHalo(Player player) {
        // 这里可以添加条件判断，比如玩家是否有特定能力、是否潜行等
        return player == Minecraft.getInstance().player; // 只为自己渲染
    }
}
