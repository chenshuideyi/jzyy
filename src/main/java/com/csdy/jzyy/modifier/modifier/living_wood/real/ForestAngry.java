package com.csdy.jzyy.modifier.modifier.living_wood.real;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
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

import javax.annotation.Nullable;
//TODO 也许可以优化
public class ForestAngry extends NoLevelsModifier implements MeleeDamageModifierHook, ArrowDamageModifierHook {

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
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow,
                                @Nullable LivingEntity attacker, @NotNull Entity target, float baseDamage, float damage) {
        if (attacker == null) return damage;

        int treeCount = countNearbyTrees(attacker.level(), attacker.blockPosition());
        float damageMultiplier = 1.0f + (10 - Math.min(treeCount, 10)) * 0.03f; // 远程加成略低
        return damage * damageMultiplier;
    }

    // 统计周围10格内的树木数量
    private int countNearbyTrees(Level level, BlockPos centerPos) {
        int radius = 10; // 检测半径
        int treeCount = 0;

        // 检测范围内的原木方块
        for (BlockPos pos : BlockPos.betweenClosed(
                centerPos.offset(-radius, -radius, -radius),
                centerPos.offset(radius, radius, radius))) {

            if (level.getBlockState(pos).is(BlockTags.LOGS)) {
                treeCount++;
            }
        }

        return treeCount;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }


}
