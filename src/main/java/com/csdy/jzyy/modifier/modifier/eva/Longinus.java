package com.csdy.jzyy.modifier.modifier.eva;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ProjectileDamageModifierHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class Longinus extends Modifier implements MeleeDamageModifierHook, ProjectileDamageModifierHook {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        int level = entry.getLevel();
        if (target == null) return baseDamage;
        float amount = 100 * (level * 2 - 1);
        return damage * amount;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.PROJECTILE_DAMAGE);
    }


    @Override
    public float getProjectileDamage(ModDataNBT modDataNBT, ModifierEntry modifierEntry, ModifierNBT modifierNBT, @NotNull Projectile projectile, @Nullable AbstractArrow abstractArrow, @Nullable LivingEntity livingEntity, @NotNull Entity entity, float v, float v1) {
        if (entity instanceof LivingEntity) {
            int level = modifierEntry.getLevel();
            float amount = 100 * (level * 2 - 1);
            return v1 * amount;
        }
        return v1;
    }
}
