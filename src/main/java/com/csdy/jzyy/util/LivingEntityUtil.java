package com.csdy.jzyy.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LivingEntityUtil {

    //DATA_HEALTH_ID
    private static final ConcurrentHashMap<Class<? extends LivingEntity>, EntityDataAccessor<Number>> HEALTH_FIELD_CACHE = new ConcurrentHashMap<>();
    public static final EntityDataAccessor<Float> DATA_HEALTH_ID = getHealthDataAccessor();
    public static final Attribute MAX_HEALTH = Attributes.MAX_HEALTH;

    //现在没问题了，我想
    private static EntityDataAccessor<Float> getHealthDataAccessor() {
        for (String fieldName : new String[]{"DATA_HEALTH_ID", "f_20961_"}) {
            try {
                Field field = LivingEntity.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(null);
                if (value instanceof EntityDataAccessor) {
                    return (EntityDataAccessor<Float>) value;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

//    private static Attributes getMaxHealthAttributes() {
//        try {
//            Class<?> attributesClass = Attributes.class;
//
//            // 获取 MAX_HEALTH 字段
////            Field field = attributesClass.getField("MAX_HEALTH");
//            Field field = LivingEntity.class.getDeclaredField("f_20961_");
//            field.setAccessible(true);
//            Object value = field.get(null);
//
//            if (value instanceof Attributes) {
//                return (Attributes) value;
//            } else {
//                System.err.println("MAX_HEALTH获取失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    ///反射设置生命值<br/>
    ///target 目标<br/>
    ///player 玩家<br/>
    ///float 设置的值<br/>
    /// 打穿王八壳,放手一搏吧<br/>
    /// 不是永远都有用,,,,,
    public static void reflectionSetHealth(Entity target, Player player, float value) {
        if (!(target instanceof LivingEntity living)) return;
        if (DATA_HEALTH_ID == null) return;
        living.getEntityData().set(DATA_HEALTH_ID, value);
        if (living.getHealth() <= 0.0F) {
            living.die(living.level().damageSources().playerAttack(player));
        }
    }

    ///反射贯穿伤害<br/>
    ///@param target 目标<br/>
    ///@param player 玩家<br/>
    ///@param value 造成的伤害<br/>
    ///  打穿王八壳,放手一搏吧<br/>
    /// 普猫无视这个，不是永远都有用,,,,,
    public static void reflectionPenetratingDamage(Entity target, Player player, float value) {
        if (!(target instanceof LivingEntity living)) return;
        if (DATA_HEALTH_ID == null) return;
        float currentHealth = living.getEntityData().get(DATA_HEALTH_ID);
        float newHealth = currentHealth-value;
        living.getEntityData().set(DATA_HEALTH_ID, newHealth);
        if (living.getHealth() <= 0.0F) {
            living.die(living.level().damageSources().playerAttack(player));
        }
    }

    ///反射最大生命切断<br/>
    ///@param target 目标<br/>
    ///@param player 玩家<br/>
    ///@param value 造成的伤害<br/>
    ///打穿王八壳,放手一搏吧<br/>
    ///不是永远都有用,,,,,
    public static void reflectionSetMaxHealth(Entity target, Player player, float value) {
        if (!(target instanceof LivingEntity living)) return;

        AttributeInstance attributeInstance = living.getAttribute(MAX_HEALTH);
        if (attributeInstance == null) return;


        attributeInstance.setBaseValue(value);
        reflectionSetHealth(target,player,value);

        // 如果生命值小于等于 0，让生物死亡
        if (value <= 0.0F) {
            living.die(living.level().damageSources().playerAttack(player));
        }
    }

    ///反射最大生命切断伤害<br/>
    ///@param target 目标<br/>
    ///@param player 玩家<br/>
    ///@param value 造成的伤害<br/>
    ///打穿王八壳,放手一搏吧<br/>
    ///不是永远都有用,,,,,
    public static void reflectionPenetratingMaxHealthDamage(Entity target, Player player, float value) {
        if (!(target instanceof LivingEntity living)) return;

        AttributeInstance attributeInstance = living.getAttribute(MAX_HEALTH);
        if (attributeInstance == null) return;

        double currentHealth = attributeInstance.getValue();
        double newHealth = currentHealth - value;

        attributeInstance.setBaseValue(newHealth);
        reflectionSetHealth(target,player, (float) newHealth);

        // 如果生命值小于等于 0，让生物死亡
        if (newHealth <= 0.0F) {
            living.die(living.level().damageSources().playerAttack(player));
        }
    }

    /**
     * 强制添加效果实例（包括自定义效果）
     * @param entity 目标生物
     * @param instance 效果实例（包含效果类型、时长、等级等）
     */
    public static void forceAddEffect(LivingEntity entity, MobEffectInstance instance) {
        // 尝试两种可能的字段名（开发环境和混淆环境）
        for (String fieldName : new String[]{"activeEffects", "f_20945_"}) {
            try {
                Field effectsField = LivingEntity.class.getDeclaredField(fieldName);
                effectsField.setAccessible(true);

                @SuppressWarnings("unchecked")
                Map<MobEffect, MobEffectInstance> effects = (Map<MobEffect, MobEffectInstance>) effectsField.get(entity);
                effects.put(instance.getEffect(), instance);

                // 如果是客户端则不需要发送数据包
                if (entity.level().isClientSide) return;

                // 如果是玩家则发送效果更新包
                if (entity instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundUpdateMobEffectPacket(
                            entity.getId(),
                            instance
                    ));
                }
                return; // 成功执行后直接返回
            } catch (Exception ignored) {}
        }

        // 所有尝试都失败时打印错误
        System.err.println("无法强制添加效果: " + instance);
    }

    /**
     * 强制清除负面效果实例（包括自定义效果）
     * @param entity 目标生物
     */
    public static void forceRemoveAllNegativeEffects(LivingEntity entity) {
        try {
            ///混淆名 f_20945_
            Field effectsField = LivingEntity.class.getDeclaredField("f_20945_");
//            Field effectsField = LivingEntity.class.getDeclaredField("activeEffects");
            effectsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<MobEffect, MobEffectInstance> effects = (Map<MobEffect, MobEffectInstance>) effectsField.get(entity);

            Iterator<Map.Entry<MobEffect, MobEffectInstance>> iterator = effects.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<MobEffect, MobEffectInstance> entry = iterator.next();
                MobEffect effect = entry.getKey();
                if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                    if (!entity.level().isClientSide && entity instanceof ServerPlayer player) {
                        player.connection.send(new ClientboundRemoveMobEffectPacket(entity.getId(), effect));
                    }
                    iterator.remove();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    ///下面写一个用来存的表，那么
    /// by C江秋
    private static final ConcurrentMap<UUID, Float> ABSOLUTE_SEVERANCE_HEALTH_MAP = new ConcurrentHashMap<>();

    /**
     * 记录某个实体在绝对切断伤害下的预期生命值。
     */
    public static void setAbsoluteSeveranceHealth(LivingEntity entity, float expectedHealth) {
        if (entity == null) return;
        UUID uuid = entity.getUUID();
        ABSOLUTE_SEVERANCE_HEALTH_MAP.put(uuid, expectedHealth);
//        System.out.println("AbsoluteSeverance: Recorded destruction health for entity " + uuid + " = " + expectedHealth);
    }

    /**
     * 获取某个实体记录的绝对绝对切断预期生命值，如果未记录则返回 Float.NaN
     */
    public static float getAbsoluteSeveranceHealth(LivingEntity entity) {
        if (entity == null) return Float.NaN;
        return ABSOLUTE_SEVERANCE_HEALTH_MAP.getOrDefault(entity.getUUID(), Float.NaN);
    }

    /**
     * 清除某个实体的绝对切断伤害记录
     */
    public static void clearAbsoluteSeveranceHealth(LivingEntity entity) {
        if (entity != null) {
            ABSOLUTE_SEVERANCE_HEALTH_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 通过 UUID 清除绝对切断伤害记录
     */
    public static void clearAbsoluteSeveranceHealth(UUID uuid) {
        if (uuid != null) {
            ABSOLUTE_SEVERANCE_HEALTH_MAP.remove(uuid);
        }
    }

    /**
     * 返回绝对切断伤害记录映射
     */
    public static ConcurrentMap<UUID, Float> getAbsoluteSeveranceHealthMap() {
        return ABSOLUTE_SEVERANCE_HEALTH_MAP;
    }

    // ---------------------------------------------
    // 内部写入标志（用于 Mixin 区分内/外部写入）
    // ---------------------------------------------
    public static class WriteFlag {
        private static final ThreadLocal<Integer> WRITE_DEPTH = ThreadLocal.withInitial(() -> 0);

        public static void beginWrite() {
            WRITE_DEPTH.set(WRITE_DEPTH.get() + 1);
        }

        public static void endWrite() {
            WRITE_DEPTH.set(WRITE_DEPTH.get() - 1);
        }

        public static boolean isInternalWrite() {
            return WRITE_DEPTH.get() > 0;
        }
    }


    ///反射设置生命值<br/>
    ///target 目标<br/>
    ///float 设置的值<br/>
    /// 仅作用于绝对切断<br/>
    public static void reflectionSeverance(Entity target, float value) {
        if (!(target instanceof LivingEntity living)) return;
        if (DATA_HEALTH_ID == null) return;
        living.getEntityData().set(DATA_HEALTH_ID, value);
    }


    /**
     * 覆盖实体所有候选生命值字段（包括 DataAccessor 和 NBT 方式），确保与预期生命值一致
     * 同时也有Attribute修改
     * 我称它为“绝对切断”（Absolute Severance）
     */
    public static void forceSetAllCandidateHealth(LivingEntity entity,float newHealth) {
        // 先尝试覆盖原版生命值
        reflectionSeverance(entity,newHealth);
        // 如果实体生命值仍不符合预期，则进一步查找候选字段并覆盖
        if (Math.abs(entity.getHealth() - newHealth) > 0.1f) {
            List<EntityDataAccessor<Number>> candidates = findCandidateHealthAccessors(entity);
            for (EntityDataAccessor<Number> accessor : candidates) {
                forceSetHealthByAccessor(entity, accessor, newHealth);
            }
            forceSetCandidateNBT(entity, newHealth);
        }
    }

    /**
     * 查找所有可能的生命值候选字段（DataAccessor），依据字段值与实体当前生命值的接近程度判断
     */
    public static List<EntityDataAccessor<Number>> findCandidateHealthAccessors(LivingEntity entity) {
        List<EntityDataAccessor<Number>> candidates = new ArrayList<>();
        if (HEALTH_FIELD_CACHE.containsKey(entity.getClass())) {
            candidates.add(HEALTH_FIELD_CACHE.get(entity.getClass()));
            return candidates;
        }
        for (Field field : entity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                    EntityDataAccessor<?> accessor = (EntityDataAccessor<?>) field.get(entity);
                    Object value = entity.getEntityData().get(accessor);
                    if (value instanceof Number numberValue) {
                        float num = numberValue.floatValue();
                        if (Math.abs(num - entity.getHealth()) < 1.0f || num == entity.getMaxHealth()) {
                            candidates.add((EntityDataAccessor<Number>) accessor);
                        }
                    }
                }
            } catch (IllegalAccessException ignored) {}
        }
        if (!candidates.isEmpty()) {
            HEALTH_FIELD_CACHE.put(entity.getClass(), candidates.get(0));
        }
        return candidates;
    }

    /**
     * 通过 DataAccessor 强制修改生命值字段
     */
    public static void forceSetHealthByAccessor(LivingEntity entity, EntityDataAccessor<Number> accessor, float newHealth) {
        Object currentValue = entity.getEntityData().get(accessor);
        WriteFlag.beginWrite();
        try {
            if (currentValue instanceof Integer) {
                entity.getEntityData().set(accessor, (int) newHealth);
            } else if (currentValue instanceof Float) {
                entity.getEntityData().set(accessor, newHealth);
            }
        } finally {
            WriteFlag.endWrite();
        }
    }

    /**
     * 通过修改 NBT 中候选字段来覆盖生命值，
     * 遍历所有数值类型键，若发现值与当前生命值接近或等于最大生命值，则修改该键后通过反射写回实体
     */
    public static void forceSetCandidateNBT(LivingEntity entity, float newHealth) {
        try {
            CompoundTag tag = entity.saveWithoutId(new CompoundTag());
            boolean modified = false;
            for (String key : tag.getAllKeys()) {
                if (tag.get(key) instanceof Number) {
                    float value = tag.getFloat(key);
                    if (Math.abs(value - entity.getHealth()) < 1.0f || value == entity.getMaxHealth()) {
                        tag.putFloat(key, newHealth);
                        modified = true;
                    }
                }
            }
            if (modified) {
                try {
//                    Method readMethod = LivingEntity.class.getDeclaredMethod("readAdditionalSaveData", CompoundTag.class);
                    Method readMethod = LivingEntity.class.getDeclaredMethod("m_7378_", CompoundTag.class);
                    //m_7378_
                    readMethod.setAccessible(true);
                    readMethod.invoke(entity, tag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
