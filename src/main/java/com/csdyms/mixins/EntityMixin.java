package com.csdyms.mixins;

import com.csdy.until.List.GodList;
import com.csdyms.core.EntityUntil;
import com.csdyms.core.enums.EntityCategory;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = Integer.MAX_VALUE)
public class EntityMixin {
    @Unique
    private EntityCategory Csdy$entityCategory = EntityCategory.normal;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (GodList.isGodPlayer(this)) EntityUntil.setCategory((Entity) (Object)this, EntityCategory.csdy);
    }
}
