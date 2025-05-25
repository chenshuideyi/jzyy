package com.csdy.jzyy.modifier.modifier.warframe1999.tool;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.LeftClickModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class Excalibur extends NoLevelsModifier implements LeftClickModifierHook {
    private static final float DASH_SPEED = 4.0f;


    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        if (!level.isClientSide) {
            Dash(player);
        }
    }

    private void Dash(Player player) {

        Vec3 dashDirection = player.getLookAngle();

        // 设置玩家冲刺状态
        player.setDeltaMovement(dashDirection.scale(DASH_SPEED));
        player.hurtMarked = true; // 强制同步运动数据,很重要

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.LEFT_CLICK);
    }
}
