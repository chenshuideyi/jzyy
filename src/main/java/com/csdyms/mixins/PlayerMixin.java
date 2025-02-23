package com.csdyms.mixins;

import com.csdy.item.register.HideRegister;
import com.csdy.until.ClassObj;
import com.csdy.until.List.GodList;
import com.csdyms.ParticleUtils;
import com.csdyms.core.EntityUntil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;


@Mixin({Player.class})
public class PlayerMixin {

    @Shadow public static int MAX_HEALTH;
    private Player player = (Player) (Object) this;

    public PlayerMixin() {
    }

    @Inject(
            method = {"tick"},
            at = {@At("HEAD")}
    )
    public void Tick(CallbackInfo ci) {
        if (this.player != null && this.player.getInventory().contains(new ItemStack(HideRegister.CSDY_SWORD.get()))) {
            GodList.SetGodPlayer(this.player);
            EntityUntil.isCsdy(this.player);
            player.hurtMarked = false;
//            this.player.setItemInHand(InteractionHand.MAIN_HAND, HideRegister.CSDY_SWORD.get().getDefaultInstance());
//            this.player.setItemInHand(InteractionHand.OFF_HAND, HideRegister.CSDY_SWORD.get().getDefaultInstance());
            if (!this.player.inventory.contains(new ItemStack(HideRegister.CSDY_SWORD.get()))) {
                this.player.inventory.add(new ItemStack(HideRegister.CSDY_SWORD.get()));

            }
            ParticleUtils.Wing(this.player,this.player.level);
            player.getAbilities().setWalkingSpeed(0.2F);
            player.getAbilities().setFlyingSpeed(1F);
            player.getAbilities().mayfly = true;
            player.hurtTime = 0;
//            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);
        }

        if (GodList.isGodPlayer(this.player)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null && !ClassObj.isAllowScreen(mc.screen)) {
                mc.screen.height = 0;
                mc.screen.width = 0;
                mc.setScreen(null);
                mc.screen = null;
            }

            if (!this.player.inventory.contains(new ItemStack(HideRegister.CSDY_SWORD.get()))) {
                this.player.inventory.add(new ItemStack(HideRegister.CSDY_SWORD.get()));
            }
            
        }

    }
    @Inject(
            method = {"getHurtSound"},
            at = {@At("RETURN")},
            cancellable = true
    )
    protected void getHurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> cir) {
            cir.setReturnValue((SoundEvent) null);
    }

}
