package com.csdy.jzyy.modifier.modifier.csdy;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import com.csdy.jzyy.ms.util.MsUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import static com.csdy.jzyy.modifier.modifier.Severance.AbsoluteSeverance.dropLoot;
import static com.csdy.jzyy.modifier.modifier.Severance.AbsoluteSeverance.setEntityDead;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromOmniMod;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceSetAllCandidateHealth;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.setAbsoluteSeveranceHealth;
import static com.csdy.jzyy.ms.util.MsUtil.KillEntity;


public class CsdyAttack extends NoLevelsModifier implements MeleeDamageModifierHook, ArrowDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        var target = context.getLivingTarget();
        if (!(context.getAttacker() instanceof Player player)) return damage;
        if (target == null) return damage;
        var playerKill = target.level().damageSources.playerAttack(player);
        forceSetAllCandidateHealth(target,0);
        setAbsoluteSeveranceHealth(target,0);
        setEntityDead(target);
        dropLoot(target,playerKill);
        target.dropAllDeathLoot(playerKill);
        CoreMsUtil.setCategory(target, EntityCategory.csdykill);
        if (isFromOmniMod(target)) KillEntity(target);
//        invokeKillEntity(target);
        return damage;
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        if (attacker instanceof ServerPlayer player && target instanceof LivingEntity living) {
            var playerKill = target.level().damageSources.playerAttack(player);
            forceSetAllCandidateHealth(living,0);
            setAbsoluteSeveranceHealth(living,0);
            setEntityDead(living);
            dropLoot(living,playerKill);
            living.dropAllDeathLoot(playerKill);
            CoreMsUtil.setCategory(living, EntityCategory.csdykill);
            if (isFromOmniMod(living)) KillEntity(living);
//            invokeKillEntity(living);
            return damage;
        }
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }

}
