package com.csdy.jzyy.modifier.modifier.csdy;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ProjectileDamageModifierHook;
import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
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

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromOmniMod;
import static com.csdy.jzyy.ms.util.MsUtil.KillEntity;


public class CsdyAttack extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        var target = context.getLivingTarget();
        if (!(context.getAttacker() instanceof Player player)) return damage;
        if (target == null) return damage;
        CoreMsUtil.setCategory(target, EntityCategory.csdykill);
        if (isFromOmniMod(target)){
            CompoundTag tag = new CompoundTag();
            tag.putFloat("Health",0);
            try {
                target.readAdditionalSaveData(tag);
            } catch (Exception ignored) {
            }
            KillEntity(target);
        }
//        invokeKillEntity(target);
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.PROJECTILE_DAMAGE);
    }

    @Override
    public float getProjectileDamage(ModDataNBT modDataNBT, ModifierEntry modifierEntry, ModifierNBT modifierNBT, @NotNull Projectile projectile, @Nullable AbstractArrow abstractArrow, @Nullable LivingEntity livingEntity, @NotNull Entity entity, float v, float v1) {
        if (livingEntity instanceof ServerPlayer player && entity instanceof LivingEntity living) {
            CoreMsUtil.setCategory(living, EntityCategory.csdykill);
            if (isFromOmniMod(living)) KillEntity(living);
//            invokeKillEntity(living);
            return v1;
        }
        return v1;
    }
}
