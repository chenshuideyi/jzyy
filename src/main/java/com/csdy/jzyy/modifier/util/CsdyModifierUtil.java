package com.csdy.jzyy.modifier.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.stat.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static AABB csdyAABB(double range, @NotNull Entity holder){
        return new AABB(holder.getX() - range, holder.getY() - range, holder.getZ() - range,
                holder.getX() + range, holder.getY() + range, holder.getZ() + range);
    }


    public static final List<Function<LivingEntity, DamageSource>> DAMAGE_SOURCE_GENERATORS = List.of(
            living -> living.damageSources().anvil(living),
            living -> living.damageSources().cactus(),
            living -> living.damageSources().cramming(),
            living -> living.damageSources().dragonBreath(),
            living -> living.damageSources().drown(),
            living -> living.damageSources().dryOut(),
            living -> living.damageSources().explosion(living, living),
            living -> living.damageSources().freeze(),
            living -> living.damageSources().fall(),
            living -> living.damageSources().fallingStalactite(living),
            living -> living.damageSources().flyIntoWall(),
            living -> living.damageSources().generic(),
            living -> living.damageSources().hotFloor(),
            living -> living.damageSources().inFire(),
            living -> living.damageSources().inWall(),
            living -> living.damageSources().indirectMagic(living, living),
            living -> living.damageSources().lava(),
            living -> living.damageSources().lightningBolt(),
            living -> living.damageSources().mobProjectile(living, living),
            living -> living.damageSources().magic(),
            living -> living.damageSources().generic(),
            living -> living.damageSources().noAggroMobAttack(living),
            living -> living.damageSources().onFire(),
            living -> living.damageSources().outOfBorder(),
            living -> {
                if (living instanceof Player player) {
                    return living.damageSources().playerAttack(player);
                }
                return living.damageSources().mobAttack(living);
            },
            living -> living.damageSources().stalagmite(),
            living -> living.damageSources().starve(),
            living -> living.damageSources().sonicBoom(living),
            living -> living.damageSources().sting(living),
            living -> living.damageSources().sweetBerryBush(),
            living -> living.damageSources().thorns(living),
            living -> living.damageSources().trident(living, living),
            living -> living.damageSources().wither()
    );

    public static List<DamageSource> damageSourcesFromForge(LivingEntity source) {
        return DAMAGE_SOURCE_GENERATORS.stream()
                .map(func -> func.apply(source))
                .collect(Collectors.toList());
    }
}
