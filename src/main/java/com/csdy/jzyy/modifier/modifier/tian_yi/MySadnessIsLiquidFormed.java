package com.csdy.jzyy.modifier.modifier.tian_yi;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MySadnessIsLiquidFormed extends NoLevelsModifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();

        if (target != null && player != null && !player.level().isClientSide) {
            Level level = player.level();
            long dayTime = level.getDayTime() % 24000;

            // 检查是否是晚上
            if (dayTime >= 12000 && dayTime < 23000) {
                // 简单氦闪效果
                level.explode(player, target.getX(), target.getY(), target.getZ(), 6.0f, true, Level.ExplosionInteraction.NONE);
                target.setSecondsOnFire(5);

                // 设置为早上
                if (level instanceof ServerLevel) ((ServerLevel) level).setDayTime((level.getDayTime() / 24000 + 1) * 24000 + 6000);


            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

}
