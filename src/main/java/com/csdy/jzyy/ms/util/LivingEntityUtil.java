package com.csdy.jzyy.ms.util;

import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromIceAndFire;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromWzzMod;

public class LivingEntityUtil {

    //DATA_HEALTH_ID
    private static final ConcurrentHashMap<Class<? extends LivingEntity>, EntityDataAccessor<Number>> HEALTH_FIELD_CACHE = new ConcurrentHashMap<>();
    public static final EntityDataAccessor<Float> DATA_HEALTH_ID = getHealthDataAccessor();
    public static final Attribute MAX_HEALTH = Attributes.MAX_HEALTH;

    //现在没问题了，我想
    private static EntityDataAccessor<Float> getHealthDataAccessor() {
        for (String fieldName : new String[]{"DATA_HEALTH_ID", "f_20961_","health"}) {
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
    ///@param attacker 攻击者<br/>
    ///@param value 造成的伤害<br/>
    ///  打穿王八壳,放手一搏吧<br/>
    /// 普猫无视这个，不是永远都有用,,,,,
    public static void reflectionPenetratingDamage(Entity target, LivingEntity attacker, float value) {
        if (!(target instanceof LivingEntity living)) return;
        if (DATA_HEALTH_ID == null) return;
        float currentHealth = living.getEntityData().get(DATA_HEALTH_ID);
        float newHealth = currentHealth-value;
        living.getEntityData().set(DATA_HEALTH_ID, newHealth);
        if (living.getHealth() <= 0.0F) {
            if (living instanceof Player player) living.die(living.level().damageSources().playerAttack(player));
            else living.die(living.level().damageSources.mobAttack(attacker));
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
     * 强制移除所有负面效果
     * @param entity 目标生物
     */
    public static void forceRemoveAllNegativeEffects(LivingEntity entity) {

        if (entity == null || entity.isRemoved()) {
            return;
        }

        boolean isServer = !entity.level().isClientSide;
        List<MobEffect> removedEffects = new ArrayList<>();
        boolean reflectionSuccess = false;
        for (String fieldName : new String[]{"activeEffects", "f_20945_"}) {
            try {
                Field effectsField = LivingEntity.class.getDeclaredField(fieldName);
                effectsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<MobEffect, MobEffectInstance> effectsMap = (Map<MobEffect, MobEffectInstance>) effectsField.get(entity);

                List<MobEffect> harmfulEffects = new ArrayList<>();
                for (MobEffect effect : effectsMap.keySet()) {
                    if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                        harmfulEffects.add(effect);
                    }
                }

                for (MobEffect effect : harmfulEffects) {
                    MobEffectInstance instance = effectsMap.remove(effect);
                    entity.removeEffect(effect);
                    if (instance != null) {
                        try {
                            Method onEffectRemoved = LivingEntity.class.getDeclaredMethod("onEffectRemoved", MobEffectInstance.class);
                            onEffectRemoved.setAccessible(true);
                            onEffectRemoved.invoke(entity, instance);
                            Method removeEffect = LivingEntity.class.getDeclaredMethod("removeEffect", MobEffect.class);
                            removeEffect.setAccessible(true);
                            removeEffect.invoke(entity, effect);

                        } catch (Exception e) {

                            try {
                                effect.removeAttributeModifiers(entity, entity.getAttributes(), instance.getAmplifier());

                            } catch (Exception ex) {

                            }
                        }

                        removedEffects.add(effect);
                    }
                }

                reflectionSuccess = true;
                break;
            } catch (Exception ex) {

                continue;
            }
        }
        if (!reflectionSuccess) {
            for (MobEffectInstance effectInstance : new ArrayList<>(entity.getActiveEffects())) {
                MobEffect effect = effectInstance.getEffect();
                if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                    entity.removeEffect(effect);
                    removedEffects.add(effect);
                }
            }
        }
        if (isServer) {


            if (entity instanceof ServerPlayer player) {
                for (MobEffect effect : removedEffects) {
                    player.connection.send(new ClientboundRemoveMobEffectPacket(entity.getId(), effect));

                }
            } else {
                MinecraftServer server = entity.level().getServer();
                if (server != null) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        for (MobEffect effect : removedEffects) {
                            player.connection.send(new ClientboundRemoveMobEffectPacket(entity.getId(), effect));
                        }
                    }
                }
            }
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
    public static void clearAbsoluteSeveranceHealth(Entity entity) {
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

    public static void SuperforceSetAllCandidateHealth(LivingEntity entity, float newHealth) {
        if (entity == null || entity.isRemoved()) {
            return;
        }

        try {

            WriteFlag.beginWrite();


            List<EntityDataAccessor<Number>> candidates = findCandidateHealthAccessors(entity);
            for (EntityDataAccessor<Number> accessor : candidates) {
                forceSetHealthByAccessor(entity, accessor, newHealth);
            }

            SuperforceSetCandidateNBTEnhanced(entity, newHealth, true);

        } catch (Exception e) {
            // 记录异常但继续执行
            e.printStackTrace();
        } finally {
            // 确保结束内部写入标记
            WriteFlag.endWrite();
        }
        if (isFromWzzMod(entity)){
            aggressivelyModifyAllHealthFields(entity,newHealth);
        }

    }

    public static void SuperforceSetCandidateNBTEnhanced(LivingEntity entity, float newHealth, boolean force) {

        if (!force && System.currentTimeMillis() - lastNbtOperationTime < 50) {
            return;
        }

        lastNbtOperationTime = System.currentTimeMillis();

        try {
            net.minecraft.nbt.CompoundTag tag = entity.saveWithoutId(new net.minecraft.nbt.CompoundTag());


            boolean modified = SuperenhanceNbtHealthModification(tag, newHealth, 3);

            if (modified) {

                java.lang.reflect.Method readMethod = getCachedReadMethod();
                if (readMethod != null) {
                    readMethod.invoke(entity, tag);
                }
            }
        } catch (Exception e) {
            // 静默处理错误
        }
    }
    private static boolean SuperenhanceNbtHealthModification(net.minecraft.nbt.CompoundTag tag, float newHealth, int maxDepth) {
        if (maxDepth <= 0) return false;

        boolean modified = false;
        java.util.Set<String> keys = new java.util.HashSet<>(tag.getAllKeys());

        for (String key : keys) {
            net.minecraft.nbt.Tag value = tag.get(key);


            if (value instanceof net.minecraft.nbt.CompoundTag compoundTag) {
                modified |= SuperenhanceNbtHealthModification(compoundTag, newHealth, maxDepth - 1);
            }
            else if (value instanceof net.minecraft.nbt.ListTag listTag) {

                for (int i = 0; i < listTag.size(); i++) {
                    net.minecraft.nbt.Tag element = listTag.get(i);
                    if (element instanceof net.minecraft.nbt.CompoundTag compoundTag) {
                        modified |= SuperenhanceNbtHealthModification(compoundTag, newHealth, maxDepth - 1);
                    }
                }
            }


            if (isNumericTag(value)) {
                setNumericTagValue(tag, key, value, newHealth);
                modified = true;
            }
        }

        return modified;
    }

    /**
     * 覆盖实体所有候选生命值字段（包括 DataAccessor 和 NBT 方式），确保与预期生命值一致
     * 同时也有Attribute修改
     * 我称它为“绝对切断”（Absolute Severance）
     */
    public static void forceSetAllCandidateHealth(LivingEntity entity, float newHealth) {

        if (entity.isDeadOrDying() || !entity.isAlive()) {
            return;
        }

        // 1. 先尝试覆盖原版生命值
        reflectionSeverance(entity, newHealth);

        // 2. 如果实体生命值仍不符合预期，则使用综合方案

            // 方法A: 使用DataAccessor查找和修改
            List<EntityDataAccessor<Number>> candidates = findCandidateHealthAccessors(entity);
            for (EntityDataAccessor<Number> accessor : candidates) {
                forceSetHealthByAccessor(entity, accessor, newHealth);
            }

            // 方法B: 使用增强的NBT遍历修改
            forceSetCandidateNBTEnhanced(entity, newHealth);


        if (isFromWzzMod(entity)){
            aggressivelyModifyAllHealthFields(entity,newHealth);
            }

    }




    public static void forceSetCandidateNBTEnhanced(LivingEntity entity, float newHealth) {

        if (System.currentTimeMillis() - lastNbtOperationTime < 50) { // 50ms冷却
            return;
        }
        lastNbtOperationTime = System.currentTimeMillis();

        try {
            CompoundTag tag = entity.saveWithoutId(new CompoundTag());
            boolean modified = enhanceNbtHealthModification(tag, entity, newHealth);

            if (modified) {

                Method readMethod = getCachedReadMethod();
                if (readMethod != null) {
                    readMethod.invoke(entity, tag);
                }
            }
        } catch (Exception e) {
            // 静默处理错误
        }
    }


    /**
     * 增强的NBT健康字段修改逻辑 - 结合精确匹配和模糊匹配
     */
    private static boolean enhanceNbtHealthModification(CompoundTag tag, LivingEntity entity, float newHealth) {
        boolean modified = false;
        float currentHealth = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        // 精确匹配已知的健康字段（避免模糊匹配）
        String[] exactHealthFields = {"Health", "health"};
        for (String field : exactHealthFields) {
            if (tag.contains(field) && isNumericTag(tag.get(field))) {
                setNumericTagValue(tag, field, tag.get(field), newHealth);
                modified = true;
                break; // 找到就退出
            }
        }

        // 只有在精确匹配失败时才进行有限递归
        if (!modified) {
            modified = limitedRecursiveModify(tag, currentHealth, maxHealth, newHealth, 2); // 限制递归深度为2
        }

        return modified;
    }

    private static Method cachedReadMethod = null;
    private static long lastNbtOperationTime = 0;

    private static Method getCachedReadMethod() {
        if (cachedReadMethod == null) {
            try {
                cachedReadMethod = LivingEntity.class.getDeclaredMethod("readAdditionalSaveData", CompoundTag.class);
                cachedReadMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                try {
                    cachedReadMethod = LivingEntity.class.getDeclaredMethod("m_7378_", CompoundTag.class);
                    cachedReadMethod.setAccessible(true);
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return cachedReadMethod;
    }

    /**
     * 递归修改NBT中的健康字段
     */
    private static boolean recursivelyModifyHealthFields(CompoundTag tag, Set<String> knownHealthFields,
                                                         float currentHealth, float maxHealth, float newHealth) {
        boolean modified = false;

        for (String key : tag.getAllKeys()) {
            Tag value = tag.get(key);

            if (value instanceof CompoundTag compoundTag) {
                // 递归处理嵌套CompoundTag
                modified |= recursivelyModifyHealthFields(compoundTag, knownHealthFields, currentHealth, maxHealth, newHealth);
            }
            else if (value instanceof ListTag listTag) {
                // 处理ListTag中的CompoundTag元素
                modified |= processHealthInListTag(listTag, knownHealthFields, currentHealth, maxHealth, newHealth);
            }
            else if (isNumericHealthField(key, value, knownHealthFields, currentHealth, maxHealth)) {
                // 修改匹配的健康字段
                setNumericTagValue(tag, key, value, newHealth);
                modified = true;
            }
        }

        return modified;
    }

    private static boolean limitedRecursiveModify(CompoundTag tag, float currentHealth, float maxHealth,
                                                  float newHealth, int maxDepth) {
        if (maxDepth <= 0) return false;

        boolean modified = false;
        Set<String> keys = new HashSet<>(tag.getAllKeys());

        for (String key : keys) {
            Tag value = tag.get(key);

            if (value instanceof CompoundTag compoundTag) {
                modified |= limitedRecursiveModify(compoundTag, currentHealth, maxHealth, newHealth, maxDepth - 1);
            }
            else if (isNumericTag(value)) {
                float numericValue = getFloatValue(value);
                // 更严格的匹配条件：数值接近当前生命值或最大生命值
                if (Math.abs(numericValue - currentHealth) < 1.0f ||
                        Math.abs(numericValue - maxHealth) < 0.1f) {
                    setNumericTagValue(tag, key, value, newHealth);
                    modified = true;
                }
            }

            if (modified) break; // 找到后就退出
        }

        return modified;
    }

    /**
     * 处理ListTag中的健康字段
     */
    private static boolean processHealthInListTag(ListTag list, Set<String> knownHealthFields,
                                                  float currentHealth, float maxHealth, float newHealth) {
        boolean modified = false;

        for (int i = 0; i < list.size(); i++) {
            Tag element = list.get(i);
            if (element instanceof CompoundTag compoundTag) {
                modified |= recursivelyModifyHealthFields(compoundTag, knownHealthFields, currentHealth, maxHealth, newHealth);
            }
        }

        return modified;
    }

    /**
     * 判断是否为健康数值字段
     */
    private static boolean isNumericHealthField(String key, Tag value, Set<String> knownHealthFields,
                                                float currentHealth, float maxHealth) {
        if (!isNumericTag(value)) {
            return false;
        }

        // 获取数值
        float numericValue = getFloatValue(value);

        // 1. 精确字段名匹配
        if (knownHealthFields.contains(key)) {
            return true;
        }

        // 2. 模糊字段名匹配（包含health、hp等关键词）
        String lowerKey = key.toLowerCase();
        boolean nameMatches = lowerKey.contains("health") ||
                lowerKey.contains("hp") ||
                lowerKey.contains("life") ||
                lowerKey.contains("hitpoint") ||
                lowerKey.contains("vital") ||
                lowerKey.contains("blood");


        boolean valueMatches = Math.abs(numericValue - currentHealth) < 1.0f ||
                Math.abs(numericValue - maxHealth) < 0.1f;

        return nameMatches || valueMatches;
    }

    /**
     * 设置数值标签的值
     */
    private static void setNumericTagValue(CompoundTag tag, String key, Tag originalValue, float newValue) {
        if (originalValue instanceof ByteTag) {
            tag.putByte(key, (byte) newValue);
        } else if (originalValue instanceof ShortTag) {
            tag.putShort(key, (short) newValue);
        } else if (originalValue instanceof IntTag) {
            tag.putInt(key, (int) newValue);
        } else if (originalValue instanceof LongTag) {
            tag.putLong(key, (long) newValue);
        } else if (originalValue instanceof FloatTag) {
            tag.putFloat(key, newValue);
        } else if (originalValue instanceof DoubleTag) {
            tag.putDouble(key, (double) newValue);
        } else if (originalValue instanceof NumericTag) {
            tag.putFloat(key, newValue);
        }
    }

    /**
     * 从Tag获取float值
     */
    private static float getFloatValue(Tag tag) {
        if (tag instanceof NumericTag numeric) {
            return numeric.getAsFloat();
        } else if (tag instanceof ByteTag byteTag) {
            return byteTag.getAsByte();
        } else if (tag instanceof ShortTag shortTag) {
            return shortTag.getAsShort();
        } else if (tag instanceof IntTag intTag) {
            return intTag.getAsInt();
        } else if (tag instanceof LongTag longTag) {
            return longTag.getAsLong();
        } else if (tag instanceof FloatTag floatTag) {
            return floatTag.getAsFloat();
        } else if (tag instanceof DoubleTag doubleTag) {
            return (float) doubleTag.getAsDouble();
        }
        return 0;
    }

    /**
     * 判断是否为数值类型的Tag
     */
    private static boolean isNumericTag(Tag tag) {
        return tag instanceof NumericTag ||
                tag instanceof ByteTag ||
                tag instanceof ShortTag ||
                tag instanceof IntTag ||
                tag instanceof LongTag ||
                tag instanceof FloatTag ||
                tag instanceof DoubleTag;
    }

    /**
     * 激进的全字段修改（备用方案）
     */
    private static void aggressivelyModifyAllHealthFields(LivingEntity entity, float newHealth) {
        try {
            CompoundTag tag = entity.saveWithoutId(new CompoundTag());
            aggressivelyModifyAllNumericFields(tag, newHealth);

            // 尝试通过反射加载修改后的数据
            try {
                Method readMethod = LivingEntity.class.getDeclaredMethod("m_7378_", CompoundTag.class);
                readMethod.setAccessible(true);
                readMethod.invoke(entity, tag);
            } catch (Exception e) {
                // 备用方案
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 激进地修改所有数值字段
     */
    private static void aggressivelyModifyAllNumericFields(CompoundTag tag, float newValue) {
        for (String key : tag.getAllKeys()) {
            Tag value = tag.get(key);

            if (value instanceof CompoundTag compound) {
                aggressivelyModifyAllNumericFields(compound, newValue);
            }
            else if (value instanceof ListTag list) {
                for (Tag element : list) {
                    if (element instanceof CompoundTag compound) {
                        aggressivelyModifyAllNumericFields(compound, newValue);
                    }
                }
            }
            else if (isNumericTag(value)) {
                setNumericTagValue(tag, key, value, newValue);
            }
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

        SynchedEntityData entityData = entity.getEntityData();
        for (Field field : entity.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                    EntityDataAccessor<?> accessor = (EntityDataAccessor<?>) field.get(entity);

                    // 检查实体数据中是否存在该访问器对应的数据项
                    if (!entityData.hasItem(accessor)) {
                        continue; // 跳过不存在的访问器
                    }

                    try {
                        Object value = entityData.get(accessor);
                        if (value instanceof Number numberValue) {
                            float num = numberValue.floatValue();
                            if (Math.abs(num - entity.getHealth()) < 1.0f || num == entity.getMaxHealth()) {
                                candidates.add((EntityDataAccessor<Number>) accessor);
                            }
                        }
                    } catch (Exception e) {
                        // 捕获获取数据时可能出现的异常，继续处理其他字段
                        continue;
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
            } else if (currentValue instanceof Boolean) {
                entity.getEntityData().set(accessor, (int) newHealth);
            } else if (currentValue instanceof Double) {
                entity.getEntityData().set(accessor, (double) newHealth);
            } else if (currentValue instanceof Long) {
                entity.getEntityData().set(accessor, (long) newHealth);
            } else if (currentValue instanceof Byte) {
                entity.getEntityData().set(accessor, (byte) newHealth);
            } else if (currentValue instanceof Short) {
                entity.getEntityData().set(accessor, (short) newHealth);
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
