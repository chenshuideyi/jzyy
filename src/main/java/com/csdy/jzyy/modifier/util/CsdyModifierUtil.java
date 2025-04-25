package com.csdy.jzyy.modifier.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.library.tools.stat.*;

import java.util.Collection;

public class CsdyModifierUtil {

    public static void multiplyAllToolStats(ModifierStatsBuilder builder, double multiplier) {

        Collection<IToolStat<?>> allStats = ToolStats.getAllStats();

        for (IToolStat<?> stat : allStats) {
            if (stat instanceof INumericToolStat<?> numericStat) {
                numericStat.multiply(builder, multiplier);
            }
        }
    }

    public static boolean isOre(BlockState state) {
        return state.is(Tags.Blocks.ORES);
    }

    private boolean isThisEntity(LivingEntity livingEntity,String string){
        String className = livingEntity.getClass().getName().toLowerCase();
        ResourceLocation id = EntityType.getKey(livingEntity.getType());
        if (id.getPath().contains(string)) return true;
        return className.contains(string);
    }
}
