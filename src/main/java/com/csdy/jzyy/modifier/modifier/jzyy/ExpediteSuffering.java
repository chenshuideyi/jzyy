package com.csdy.jzyy.modifier.modifier.jzyy;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.reflectionPenetratingDamage;

///记得补完
public class ExpediteSuffering extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget(); // 获取被攻击的目标生物
        LivingEntity attacker = context.getAttacker();
        if (target != null && !target.level().isClientSide) {
            ExpediteSuffering(target,attacker);
        }
    }


    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (target != null){
            ExpediteSuffering(target,attacker);
        }
        return false;
    }


    private static void ExpediteSuffering(LivingEntity target, LivingEntity attacker){
        int debuffCount = 0;
        List<MobEffect> debuffsToRemove = new ArrayList<>(); // 用来存储要移除的debuff类型

        Collection<MobEffectInstance> activeEffects = target.getActiveEffects();

        // 1. 遍历目标身上的所有效果，统计debuff数量并记录下来
        for (MobEffectInstance effectInstance : activeEffects) {
            MobEffect effect = effectInstance.getEffect(); // 获取效果类型
            if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                debuffCount++;
                debuffsToRemove.add(effect); // 添加到待移除列表
            }
        }

        if (debuffCount > 0) {
            for (MobEffect effectToRemove : debuffsToRemove) {
                target.removeEffect(effectToRemove);
            }
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 0.7F, 1.5F + target.level().random.nextFloat() * 0.2F);
            reflectionPenetratingDamage(target,attacker,factorial(debuffCount));
        }
    }

    private static long factorial(long number) {
        if (number <= 1)
            return 1;
        else
            return number * factorial(number - 1);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }
}
