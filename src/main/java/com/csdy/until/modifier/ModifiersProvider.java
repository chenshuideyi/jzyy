package com.csdy.until.modifier;

import com.csdy.ModMain;
import com.csdy.item.register.ItemRegister;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class ModifiersProvider extends GlobalLootModifierProvider {
    public ModifiersProvider(PackOutput output){
        super(output, ModMain.MODID);
    }

    @Override
    protected void start(){
        add("test",new AddItemModifier(new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.GRASS).build(),
                LootItemRandomChanceCondition.randomChance(0.9F).build()},
                ItemRegister.BROKEN_SACRED_RELIC.get()));

    }

}
