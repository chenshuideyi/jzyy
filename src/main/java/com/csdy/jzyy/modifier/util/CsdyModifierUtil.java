package com.csdy.jzyy.modifier.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
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

    public static void SelfExplosion(Level level, int entryLevel, LivingEntity holder) {
        // 确保只在服务端执行且持有者存活
        if (level.isClientSide || !holder.isAlive()) return;

        DamageSource source = level.damageSources().source(DamageTypes.EXPLOSION);

        // 根据entryLevel决定爆炸属性
        float power = 4.0f + entryLevel * 2.0f; // 基础4，每级增加2
        boolean causesFire = entryLevel >= 3; // 3级以上爆炸产生火焰
        Explosion.BlockInteraction mode = entryLevel >= 5 ?
                Explosion.BlockInteraction.DESTROY_WITH_DECAY : // 5级以上破坏方块并有衰减效果
                Explosion.BlockInteraction.DESTROY; // 默认破坏方块

        // 创建爆炸对象（使用现代Minecraft构造函数）
        Explosion explosion = new Explosion(
                level,
                holder, // 爆炸源实体// 如果是生物则使用生物攻击伤害源
                source,
                null, // 爆炸伤害计算器（可为null）
                holder.getX(), // 爆炸X坐标
                holder.getY(), // 爆炸Y坐标
                holder.getZ(), // 爆炸Z坐标
                power, // 爆炸威力
                causesFire, // 是否产生火焰
                mode // 方块破坏模式
        );

        // 执行爆炸
        explosion.explode();
        explosion.finalizeExplosion(true);

        // 播放爆炸音效（使用holder的位置）
        level.playSound(
                null,
                holder.getX(),
                holder.getY(),
                holder.getZ(),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE, // 使用HOSTILE音源分类更合适
                4.0F,
                (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F
        );

        // 可选：对持有者造成伤害（自爆效果）
        if (entryLevel >= 2) {
            holder.hurt(level.damageSources().explosion(explosion), Float.MAX_VALUE);
        }
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

    /**
     * 检测实体是否是假人
     */
    public static boolean isFromDummmmmmyMod(Entity entity) {
        if (entity == null) {
            return false;
        }
        // 方法1：检查实体的命名空间（推荐）
        ResourceLocation entityId = EntityType.getKey(entity.getType());
        if (entityId != null && entityId.getNamespace().equals("dummmmmmy")) {
            return true;
        }

        // 方法2：检查实体的类路径（备用方案）
        return entity.getClass().getName().contains("dummmmmmy");
    }


}
