package com.csdy.jzyy.modifier.modifier.living_wood.real;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ProjectileDamageModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

//TODO 也许可以优化
public class ForestAngry extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        if (attacker == null) return damage;

        // 获取周围树木数量
        int treeCount = countNearbyTrees(attacker.level(), attacker.blockPosition());

        // 计算伤害加成 (每少一棵树增加5%伤害，最多10棵树50%加成)
        float damageMultiplier = 1.0f + (10 - Math.min(treeCount, 10)) * 0.05f;
        return damage * damageMultiplier;
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.PROJECTILE_DAMAGE);
    }


    @Override
    public float getProjectileDamage(ModDataNBT modDataNBT, ModifierEntry modifierEntry, ModifierNBT modifierNBT, @NotNull Projectile projectile, @org.jetbrains.annotations.Nullable AbstractArrow abstractArrow, @org.jetbrains.annotations.Nullable LivingEntity livingEntity, @NotNull Entity entity, float v, float v1) {
        if (livingEntity == null) return v1;

        int treeCount = countNearbyTrees(livingEntity.level(), livingEntity.blockPosition());
        float damageMultiplier = 1.0f + (10 - Math.min(treeCount, 10)) * 0.03f;
        return v1 * damageMultiplier;
    }

    private int countNearbyTrees(Level level, BlockPos centerPos) {
        int radius = 10;
        int treeCount = 0;

        for (BlockPos pos : BlockPos.betweenClosed(
                centerPos.offset(-radius, -radius, -radius),
                centerPos.offset(radius, radius, radius))) {

            if (level.getBlockState(pos).is(BlockTags.LOGS)) {
                treeCount++;
            }
        }

        return treeCount;
    }
}