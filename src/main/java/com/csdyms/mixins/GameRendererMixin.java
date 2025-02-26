package com.csdyms.mixins;

import com.csdy.until.List.GodList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At(value = "HEAD"),method = "bobHurt",cancellable = true)
    public void cancleShaking(PoseStack p_109118_, float p_109119_, CallbackInfo ci){
        Entity entity = ((GameRenderer) (Object) this).getMinecraft().getCameraEntity();
        if (GodList.isGodPlayer(entity)){
            ci.cancel();
        }
    }

}
