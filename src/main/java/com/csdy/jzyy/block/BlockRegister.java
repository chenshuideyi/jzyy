package com.csdy.jzyy.block;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.csdy.jzyy.JzyyModMain.MODID;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

/*    public static final RegistryObject<Block> HARCADIUM_ORE = BLOCKS.register(
            "harcadium_ore",
            HarcadiumOre::new
    );

    public static final RegistryObject<Block> HARCADIUM_ORE_END_STONE = BLOCKS.register(
            "harcadium_ore_end_stone",
            HarcadiumOreEndStone::new
    );

*/
}
