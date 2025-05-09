package com.csdy.jzyy.modifier.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.library.tools.stat.*;

import java.lang.reflect.Field;
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

    public static void addAllToolStats(ModifierStatsBuilder builder, double multiplier) {

        Collection<IToolStat<?>> allStats = ToolStats.getAllStats();

        for (IToolStat<?> stat : allStats) {
            if (stat instanceof INumericToolStat<?> numericStat) {
                numericStat.add(builder, multiplier);
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

    public boolean isBoss(LivingEntity living) {
        try {
            Class<?> clazz = living.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == ServerBossEvent.class) {
                    field.setAccessible(true);
                    ServerBossEvent bossEvent = (ServerBossEvent) field.get(living);
                    if (bossEvent != null) {
                        return true;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.err.println("反射坠机，无法获取ServerBossEvent字段");
        }
        return false;
    }

    public static boolean isEntityInSunlight(Entity entity) {
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();

        // 检查是否是白天 + 天空可见 + 天空光照足够高
        return level.isDay()
                && level.canSeeSky(pos)
                && level.getBrightness(LightLayer.SKY, pos) >= 14;
    }

}
