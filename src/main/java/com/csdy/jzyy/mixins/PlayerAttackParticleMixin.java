package com.csdy.jzyy.mixins;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerAttackParticleMixin {

    // 拦截对 this.crit(pTarget) 的调用，阻止暴击粒子生成
    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"
            ),
            cancellable = true
    )
    private void cancelCritParticles(Entity pTarget, CallbackInfo ci) {
        ci.cancel(); // 取消原始调用
    }

    // 拦截对 this.magicCrit(pTarget) 的调用，阻止附魔（锋利等）伤害粒子生成
    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;magicCrit(Lnet/minecraft/world/entity/Entity;)V"
            ),
            cancellable = true
    )
    private void cancelMagicCritParticles(Entity pTarget, CallbackInfo ci) {
        ci.cancel(); // 取消原始调用
    }

    // 拦截对 this.sweepAttack() 的调用，阻止横扫攻击粒子生成
    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V"
            ),
            cancellable = true
    )
    private void cancelSweepParticles(CallbackInfo ci) {
        ci.cancel(); // 取消原始调用
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            ),
            cancellable = true
    )
    private void cancelDamageIndicatorParticles(Entity pTarget, CallbackInfo ci) {
        System.out.println("成功拦截伤害粒子！Damage particle injection successful!");
        ci.cancel(); // 取消原始调用
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            )
    )
    private int cancelDamageIndicatorParticles(ServerLevel instance, ParticleOptions options, double x, double y, double z, int count, double xd, double yd, double zd, double speed) {
        // 什么都不做，直接返回一个默认值（比如 0）。
        // 这样原始的 sendParticles 就不会被执行了。
        return 0;
    }

}
