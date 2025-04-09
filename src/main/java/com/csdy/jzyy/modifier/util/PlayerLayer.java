package com.csdy.jzyy.modifier.util;

import com.csdy.jzyy.effect.JzyyEffectRegister;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerLayer extends RenderLayer {
    //凋零护甲
    private static final ResourceLocation CREEPER_ARMOR_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final HumanoidModel<Player> model;

    public PlayerLayer(RenderLayerParent p_174554_) {
        super(p_174554_);
        Minecraft mc = Minecraft.getInstance();
        EntityRendererProvider.Context context = new EntityRendererProvider.Context(
                mc.getEntityRenderDispatcher(),mc.getItemRenderer(),mc.blockRenderer,mc.gameRenderer.itemInHandRenderer, mc.getResourceManager(),mc.getEntityModels(),mc.font
        );
        this.model = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
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
    public void render(PoseStack stack, MultiBufferSource bufferSource, int p_116972_, Entity entity, float p_116974_, float p_116975_, float tick, float p_116977_, float p_116978_, float p_116979_) {
        Player player = (Player) entity;
        if (!player.hasEffect(JzyyEffectRegister.OVERCHARGE_ARMOR.get())) return;
        float $$10 = (float)entity.tickCount + tick;
        EntityModel<Player> playerEntityModel = this.model();
        playerEntityModel.prepareMobModel((Player) entity, p_116974_, p_116975_, tick);
        this.getParentModel().copyPropertiesTo(playerEntityModel);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset($$10) % 1.0F, $$10 * 0.01F % 1.0F));
        playerEntityModel.setupAnim((Player) entity, p_116974_, p_116975_, p_116977_, p_116978_, p_116979_);
        playerEntityModel.renderToBuffer(stack, consumer, p_116972_, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);

    }
}
