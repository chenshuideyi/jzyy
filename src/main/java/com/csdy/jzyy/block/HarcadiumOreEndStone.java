package com.csdy.jzyy.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class HarcadiumOreEndStone extends Block {
    public HarcadiumOreEndStone() {
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
