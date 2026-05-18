package com.csdy.jzyy.modifier.modifier.bian;

import com.csdy.jzyy.ms.util.LivingEntityUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.*;

public class DotModifier extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook {

    private static final float ABSOLUTE_SEVERANCE_THRESHOLD = 1F;
    private static final float DAMAGE_MULTIPLIER_PER_EFFECT = 212121212121F;
    private static final Map<UUID, Integer> HIGHEST_EFFECT_COUNT_MAP = new ConcurrentHashMap<>();
    private static final List<MobEffect> HARMFUL_EFFECTS_CACHE;
    static {
        List<MobEffect> effects = new ArrayList<>();
        for (var key : ForgeRegistries.MOB_EFFECTS.getKeys()) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
            if (effect != null && effect.getCategory() == MobEffectCategory.HARMFUL) {
                effects.add(effect);
            }
        }
        HARMFUL_EFFECTS_CACHE = Collections.unmodifiableList(effects);

        MinecraftForge.EVENT_BUS.register(DotModifier.class);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity enemy = context.getLivingTarget();
        if (enemy == null) return damage;
        int level = modifier.getLevel();
        List<MobEffect> statusEffects = getRandomStatusEffects(level);
        applyStatusEffects(enemy, statusEffects, level);
        int currentEffectCount = enemy.getActiveEffects().size();
        int highestEffectCount = updateHighestEffectCount(enemy.getUUID(), currentEffectCount);
        float damageMultiplier = getDamageMultiplier(highestEffectCount);
        return baseDamage * damageMultiplier;
    }
    @Override
    public float beforeMeleeHit(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity enemy = context.getLivingTarget();
        if (enemy == null) return knockback;
        int highestEffectCount = HIGHEST_EFFECT_COUNT_MAP.getOrDefault(enemy.getUUID(), 0);
        if (highestEffectCount < ABSOLUTE_SEVERANCE_THRESHOLD) return knockback;
        Player attacker = context.getPlayerAttacker();
        if (attacker == null) return knockback;
        if (enemy instanceof Player) return knockback;
        if (enemy.getHealth() <= 0) return knockback;
        if (isFromDummmmmmyMod(enemy)) return knockback;
        if (isDefender(enemy)) return knockback;
        float damageMultiplier = getDamageMultiplier(highestEffectCount);
        float toolDmg = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
        float extraSeverance = highestEffectCount * highestEffectCount * highestEffectCount * 1.2F + damageMultiplier;
        modifierAbsoluteSeverance(enemy, attacker, toolDmg, extraSeverance);
        return knockback;
    }

    @Override
    public void failedMeleeHit(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity enemy = context.getLivingTarget();
        if (enemy == null) return ;
        int highestEffectCount = HIGHEST_EFFECT_COUNT_MAP.getOrDefault(enemy.getUUID(), 0);
        if (highestEffectCount < ABSOLUTE_SEVERANCE_THRESHOLD) return ;
        Player attacker = context.getPlayerAttacker();
        if (attacker == null) return ;
        if (enemy instanceof Player) return ;
        if (enemy.getHealth() <= 0) return ;
        if (isFromDummmmmmyMod(enemy)) return ;
        if (isDefender(enemy)) return;
        float damageMultiplier = getDamageMultiplier(highestEffectCount);
        float extraSeverance = highestEffectCount * highestEffectCount * highestEffectCount * 1.2F + damageMultiplier;
        modifierAbsoluteSeverance(enemy, attacker, tool.getDamage(), extraSeverance);
    }

    private static float getDamageMultiplier(float effectCount) {
        return 1.0F + effectCount * DAMAGE_MULTIPLIER_PER_EFFECT;
    }

    private static List<MobEffect> getRandomStatusEffects(int count) {
        if (HARMFUL_EFFECTS_CACHE.isEmpty() || count <= 0) return Collections.emptyList();
        List<MobEffect> shuffled = new ArrayList<>(HARMFUL_EFFECTS_CACHE);
        Collections.shuffle(shuffled, ThreadLocalRandom.current());
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    private static void applyStatusEffects(LivingEntity entity, List<MobEffect> statusEffects, int level) {
        int duration = level * 20 * 100;
        for (MobEffect effect : statusEffects) {
            MobEffectInstance instance = new MobEffectInstance(effect, duration, 4);
            try {
                LivingEntityUtil.forceAddEffect(entity, instance);
            } catch (Exception e) {
                entity.addEffect(instance);
            }
        }
    }
    private static int updateHighestEffectCount(UUID entityId, int currentCount) {
        return HIGHEST_EFFECT_COUNT_MAP.merge(entityId, currentCount, Math::max);
    }
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        HIGHEST_EFFECT_COUNT_MAP.clear();
    }
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        HIGHEST_EFFECT_COUNT_MAP.remove(event.getEntity().getUUID());
    }
}