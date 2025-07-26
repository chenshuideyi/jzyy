package com.csdy.jzyy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class HarcadiumOre extends Block {
    public HarcadiumOre() {
        super(
                Properties.of()
                        .strength(50.0f, 2000.0f)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.STONE)
        );
    }

    ///会导致匠魂镐无法挖掘
//    @Override
//    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
//        ItemStack tool = player.getMainHandItem();
//
//        if (tool.getItem() instanceof PickaxeItem pickaxe) {
//            return pickaxe.getTier().getLevel() >= 3;
//        }
//        return false;
//    }

}
