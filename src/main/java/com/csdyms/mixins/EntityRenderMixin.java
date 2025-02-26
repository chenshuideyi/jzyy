package com.csdyms.mixins;

import com.csdy.until.List.DeadLists;
import com.csdy.until.List.GodList;
import com.csdy.until.ReClass.ReFont;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


//@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRenderMixin<T extends Entity>{

    @Inject(method = "render" , at = @At("HEAD"),cancellable = true)
    private void render(T entity, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_, CallbackInfo ci){
        if (DeadLists.isEntity(entity)){
            ci.cancel();
        }
    }

    @Inject(method = "shouldShowName" , at = @At("RETURN"),cancellable = true)
    private void shouldShowName(T target,CallbackInfoReturnable<Boolean> ci){
        if (GodList.isGodPlayer(target)){
            ci.setReturnValue(Boolean.TRUE);
        }
    }


    @Inject(method = "getFont" , at = @At("RETURN"),cancellable = true)
    private void getFont(CallbackInfoReturnable<Font> ci){
        if (GodList.isGodPlayer(Minecraft.getInstance().player)){
            ci.setReturnValue(ReFont.getFont());
        }
    }
}
