package com.csdy.jzyy.modifier.modifier.harcadium;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.LeftClickModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EatStone extends NoLevelsModifier implements LeftClickModifierHook {

    @Override
    public void onLeftClickBlock(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        Block block = state.getBlock();

        // 特殊处理：基岩（Bedrock）强制掉落
        if (block == Blocks.BEDROCK) {
            level.destroyBlock(pos, false);
            if (!level.isClientSide) {
                ItemStack bedrockStack = new ItemStack(Blocks.BEDROCK);
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX() + 0.5,  // 中心位置
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        bedrockStack
                );
                level.addFreshEntity(itemEntity);
            }
        }

        if (state.is(BlockTags.STONE_ORE_REPLACEABLES) ||
                state.is(BlockTags.BASE_STONE_OVERWORLD) ||
                state.is(BlockTags.BASE_STONE_NETHER) ||
                state.is(Blocks.END_STONE) ||
                state.is(Blocks.COBBLESTONE)
        ) {
            level.destroyBlock(pos, false);
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.LEFT_CLICK);
    }


}
