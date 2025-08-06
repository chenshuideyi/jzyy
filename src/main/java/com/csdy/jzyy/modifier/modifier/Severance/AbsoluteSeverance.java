package com.csdy.jzyy.modifier.modifier.Severance;


import com.csdy.jzyy.JzyyModMain;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromDummmmmmyMod;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.*;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbsoluteSeverance extends NoLevelsModifier implements MeleeHitModifierHook {
    //TODO 切断死目标凋落物
    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    private final float value;

    public AbsoluteSeverance(float value) {
        this.value = value;
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null && target.getHealth() > 0) {
            if (isFromDummmmmmyMod(target)) return knockback;
            float reHealth = target.getHealth() - damage * this.value - target.getMaxHealth() * 0.01f;
            setAbsoluteSeveranceHealth(target,reHealth);
            forceSetAllCandidateHealth(target,reHealth);
            if (reHealth <= 0){
                //并非不能掉落
                var playerKill = target.level().damageSources.playerAttack(player);
                target.die(playerKill);
                triggerKillAdvancement(target,playerKill);
                setEntityDead(target);
                dropLoot(target,playerKill);
            }
        }
        return knockback;
    }

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
        for (UUID uuid : getAbsoluteSeveranceHealthMap().keySet()) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                float expected = getAbsoluteSeveranceHealth(living);
                if (!Float.isNaN(expected) && Math.abs(living.getHealth() - expected) > 0.1f) {
                    forceSetAllCandidateHealth(living, expected);
                }
            } else {
                clearAbsoluteSeveranceHealth(uuid);
            }
        }
    }


}
