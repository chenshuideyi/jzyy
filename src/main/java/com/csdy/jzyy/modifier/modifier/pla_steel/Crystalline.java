package com.csdy.jzyy.modifier.modifier.pla_steel;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import com.csdy.jzyy.ModMain;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class Crystalline extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileLaunchModifierHook,ArrowDamageModifierHook {
    private static final String DAMAGE_UP = ModMain.MODID + ":damage_up";

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        int level = entry.getLevel();
        if (target == null) return baseDamage;
        if (attacker instanceof Player player) {
            return damage + ((float) tool.getCurrentDurability() / 200);
        }
        return damage;
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry entry, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT data, boolean primary) {
        if (!(shooter instanceof Player) || arrow == null) return;

        int durabilityValue = tool.getCurrentDurability() / 200;
        arrow.getPersistentData().putInt(DAMAGE_UP, durabilityValue);
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        if (attacker instanceof Player && target instanceof LivingEntity) {
            int damageBonus = arrow.getPersistentData().getInt(DAMAGE_UP);
            return damage + damageBonus;
        }
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }

}
