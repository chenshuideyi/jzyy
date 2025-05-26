package com.csdy.jzyy.modifier.modifier.ice_and_fire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Optional;

public class DragonBone extends NoLevelsModifier implements BreakSpeedModifierHook {

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event,
                             Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        Player player = event.getEntity();
        Level level = player.level;

        Optional<BlockPos> optionalPos = event.getPosition();

        if (optionalPos.isPresent()) {
            BlockPos pos = optionalPos.get();

            if (!level.isClientSide && level.getBlockState(pos).getDestroySpeed(level, pos) >= 0) {
                level.destroyBlock(pos, true, player);
            }
        }
        tool.setDamage(tool.getDamage()+1);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BREAK_SPEED);
    }


}
