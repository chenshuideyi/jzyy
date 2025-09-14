package com.csdy.jzyy.modifier.modifier.Severance;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.*;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.*;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.setAbsoluteSeveranceHealth;
import static com.mojang.text2speech.Narrator.LOGGER;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbsoluteSeverance extends NoLevelsModifier implements MeleeHitModifierHook {
    //TODO 切断死目标凋落物
    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    private final float value;
    private final float baseDamage;

    public AbsoluteSeverance(float value,float baseDamage) {
        this.value = value;
        this.baseDamage = baseDamage;
    }

    private static final Map<UUID, Long> NULL_ENTITY_TIMES = new HashMap<>();

//    @Override
//    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
//        LivingEntity target = context.getLivingTarget();
//        Player player = context.getPlayerAttacker();
//        if (target != null && player != null && target.getHealth() > 0) {
//            if (target.getHealth() <= 0) return damage;
//            if (isFromDummmmmmyMod(target)) return damage;
//            if (isDefender(target)) return damage;
//            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
//            modifierAbsoluteSeverance(target,player,toolDamage,this.value + 1);
//        }
//        return damage;
//    }


    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        var target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (isFromIceAndFire(target)) return;
        if (target instanceof LivingEntity && player != null && target.getHealth() > 0) {
            if (target.getHealth() <= 0) return;
            if (isFromDummmmmmyMod(target)) return;
            if (isDefender(target)) return;
            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            modifierAbsoluteSeverance(target,player,toolDamage,this.value + this.baseDamage);
        }
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        var target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (isFromIceAndFire(target)) return knockback;
        if (target instanceof LivingEntity && player != null && target.getHealth() > 0) {
            if (target.getHealth() <= 0) return damage;
            if (isFromDummmmmmyMod(target)) return damage;
            if (isDefender(target)) return damage;
            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            modifierAbsoluteSeverance(target,player,toolDamage,this.value + this.baseDamage);
        }
        return knockback;
    }

//    @Override
//    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
//        LivingEntity target = context.getLivingTarget();
//        Player player = context.getPlayerAttacker();
//        if (target != null && player != null && target.getHealth() > 0) {
//            if (target.getHealth() <= 0) return;
//            if (isFromDummmmmmyMod(target)) return;
//            float toolDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
//            modifierAbsoluteSeverance(target,player,toolDamage,this.value);
//        }
//    }

    /**
     * 手动触发 Advancements (成就)
     */
    public static void triggerKillAdvancement(LivingEntity target, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(player, target, source);
        }
    }

    /**
     * 确保实体被正确标记为死亡
     */
    public static void setEntityDead(LivingEntity entity) {
        try {
            Field deadField = ObfuscationReflectionHelper.findField(LivingEntity.class, "f_20890_"); // isDead
            deadField.setBoolean(entity, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理实体掉落的战利品
     */
    public static void dropLoot(LivingEntity entity, DamageSource ds) {
        try {
            Method dropAllDeathLootMethod = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_6668_", DamageSource.class);
            dropAllDeathLootMethod.invoke(entity, ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
//        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    /**
     * 监听世界刻更新：每刻在服务器端遍历绝对绝对切断伤害记录，
     * 如果对应实体存在且当前生命值与预期不符，则持续覆盖其生命值；
     * 若实体不存在，则自动清除记录。
     */
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel serverLevel)) return;

        // 使用迭代器安全地遍历
        Iterator<UUID> iterator = getAbsoluteSeveranceHealthMap().keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Entity entity = serverLevel.getEntity(uuid);

            if (entity == null) {
                // 实体为null，但先不立即移除，等待一段时间确认
                if (shouldRemoveNullEntity(uuid)) {
                    iterator.remove();
                    clearAbsoluteSeveranceHealth(uuid);
                    System.out.println("确认实体不存在，移除UUID: " + uuid);
                }
                continue;
            }

            if (entity instanceof LivingEntity living) {
                // 关键修改：只要实体还存在（即使死亡状态），效果就持续
                if (!living.isRemoved()) {
                    applyAbsoluteSeveranceEffect(living);
                } else {
                    // 实体已被彻底移除（从世界中删除），才清理记录
                    iterator.remove();
                    clearAbsoluteSeveranceHealth(uuid);
                    System.out.println("实体被彻底移除，移除UUID: " + uuid);
                }
            } else {
                iterator.remove();
                clearAbsoluteSeveranceHealth(uuid);
                System.out.println("实体不是生物，移除UUID: " + uuid);
            }
        }
    }

     ///判断是否应该移除null实体（延迟移除机制）
    private static boolean shouldRemoveNullEntity(UUID uuid) {
        // 使用一个Map来记录实体为null的首次发现时间

        long currentTime = System.currentTimeMillis();

        if (!NULL_ENTITY_TIMES.containsKey(uuid)) {
            // 第一次发现该实体为null，记录时间
            NULL_ENTITY_TIMES.put(uuid, currentTime);
            return false;
        }

        long firstNullTime = NULL_ENTITY_TIMES.get(uuid);
        long elapsedTime = currentTime - firstNullTime;

        // 如果实体持续为null超过5秒，认为已彻底消失
        if (elapsedTime > 5000) {
            NULL_ENTITY_TIMES.remove(uuid);
            return true;
        }

        return false;
    }

    /**
     * 应用绝对切断效果的核心逻辑
     */
    private static void applyAbsoluteSeveranceEffect(LivingEntity living) {
        float expected = getAbsoluteSeveranceHealth(living);

        if (Float.isNaN(expected)) {
            return;
        }

        float actualHealth = living.getHealth();

        // 记录初始状态（用于调试）
        float originalHealth = actualHealth;

        // 核心逻辑：确保血量不超过预期值
        if (actualHealth > expected) {
            // 强制降低到预期值
            forceSetAllCandidateHealth(living, expected);
            actualHealth = expected;
        }

        // 更新预期血量（如果需要）
        if (actualHealth < expected) {
            setAbsoluteSeveranceHealth(living, actualHealth);
        }


        // 调试日志
        if (originalHealth != actualHealth) {
            LOGGER.debug("绝对切断生效: {} 血量 {} -> {} (预期: {})",
                    living.getName().getString(), originalHealth, actualHealth, expected);
        }
    }


}
