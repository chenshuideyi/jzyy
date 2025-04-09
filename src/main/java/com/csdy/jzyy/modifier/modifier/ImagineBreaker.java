package com.csdy.jzyy.modifier.modifier;


import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ImagineBreaker extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileHitModifierHook {


    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            Level level = player.level();
            imagineBreaker(target);
            level.playSound(null, player.blockPosition, JzyySoundsRegister.IMAGINE_BREAKER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return damage;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT data, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity shooter, @javax.annotation.Nullable LivingEntity target) {
        if (projectile instanceof AbstractArrow arrow && target != null) {
            Level level = shooter.level();
            imagineBreaker(target);
            level.playSound(null, shooter.blockPosition, JzyySoundsRegister.IMAGINE_BREAKER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return false;
    }

    ///把怪的属性回退到基础值
    ///也就是说，卸掉恶意给的额外属性
    ///和你的等级加成说再见吧
    /// 我称它为“幻想杀手”
    private static void imagineBreaker(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (entity instanceof ServerPlayer player){
            player.connection.send(new ClientboundUpdateAttributesPacket(
                    entity.getId(),
                    entity.getAttributes().getSyncableAttributes()
            ));
        }
        entity.getAttributes().assignValues(entity.getAttributes());
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }


}
