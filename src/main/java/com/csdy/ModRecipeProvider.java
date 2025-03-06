package com.csdy;


import com.csdy.modifier.diadema.MeltDown;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput generator) {
        super(generator);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // 检查等价交换模组是否加载
        boolean isProjectELoaded = ModList.get().isLoaded("projecte");

        if (!isProjectELoaded) {
            // 等价交换未加载时的合成表
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DIAMOND)
                    .pattern("GGG")
                    .pattern("G G")
                    .pattern("GGG")
                    .define('G', Items.GOLD_INGOT)
                    .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                    .save(consumer, "diamond_from_gold_without_projecte");
        }
        else System.out.print("111111111111111111111111111111nonononononononono");
    }
}