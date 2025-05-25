package com.csdy.jzyy.modifier.modifier.warframe1999.tool;

import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.c2h6s.etstlib.entity.specialDamageSources.PercentageBypassArmorSource;
import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import com.c2h6s.etstlib.tool.hooks.ModifyDamageSourceModifierHook;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
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

import java.util.List;
import java.util.Random;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.damageSourcesFromForge;
import static java.lang.Math.pow;

public class Cyte09 extends NoLevelsModifier implements MeleeDamageModifierHook, ArrowDamageModifierHook, ModifyDamageSourceModifierHook {

    private final float DamageBoost = 1.25f;

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        return damage * DamageBoost;
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        if (target instanceof Entity) {
            return damage * DamageBoost;
        }
        return damage;
    }

    @Override
    public LegacyDamageSource modifyDamageSource(IToolStackView tool, ModifierEntry entry, LivingEntity attacker, InteractionHand hand, Entity target, EquipmentSlot sourceSlot, boolean isFullyCharged, boolean isExtraAttack, boolean isCritical, LegacyDamageSource source){
        List<DamageSource> sources = damageSourcesFromForge(attacker);
        DamageSource selected = sources.get(new Random().nextInt(sources.size()));
        return new LegacyDamageSource(selected);
    }

    @Override
    public LegacyDamageSource modifyArrowDamageSource(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, AbstractArrow arrow, @Nullable LivingEntity attacker, @Nullable Entity target, LegacyDamageSource source){
        if (attacker != null) {
            List<DamageSource> sources = damageSourcesFromForge(attacker);
            DamageSource selected = sources.get(new Random().nextInt(sources.size()));
            return new LegacyDamageSource(selected);
        }
        return source;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.MODIFY_DAMAGE_SOURCE);
    }
}
