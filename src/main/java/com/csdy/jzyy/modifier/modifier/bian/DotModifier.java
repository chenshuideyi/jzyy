package com.csdy.jzyy.modifier.modifier.bian;

import com.csdy.jzyy.ms.util.LivingEntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nonnull;
import java.util.*;
import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.modifierAbsoluteSeverance;

public class DotModifier extends Modifier implements MeleeDamageModifierHook {

    private final float absoluteSeveranceBaseValue = 1.5F;
    private final float absoluteSeveranceThreshold = 5F;

    private static final Map<UUID, Integer> highestEffectCountMap = new WeakHashMap<>();


    private static final Map<UUID, Set<MobEffect>> appliedEffectsMap = new WeakHashMap<>();


    private static final Map<UUID, Long> NULL_ENTITY_TIMES = new HashMap<>();

    private static List<MobEffect> harmfulEffectsCache = null;


    static {
        MinecraftForge.EVENT_BUS.register(DotModifier.class);
    }
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);

        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity enemy = context.getLivingTarget();
        if (enemy != null) {
            UUID enemyId = enemy.getUUID();
            int level = modifier.getLevel();

            List<MobEffect> statusEffects = getRandomStatusEffects(level);
            applyStatusEffects(enemy, statusEffects, level, enemyId);

            int currentEffectCount = enemy.getActiveEffects().size();
            int highestEffectCount = getHighestEffectCount(enemyId, currentEffectCount);

            float damageMultiplier = getDamageMultiplier(Math.max(currentEffectCount, highestEffectCount));
            damage = baseDamage * damageMultiplier;


            Player attacker = context.getPlayerAttacker();
            if (attacker != null && enemy.getHealth() > 0) {
                float weaponDamage = tool.getDamage();


                float severanceValue = absoluteSeveranceBaseValue + (highestEffectCount * 1.2F);


                modifierAbsoluteSeverance(enemy, attacker, weaponDamage, severanceValue);


                if (highestEffectCount >= absoluteSeveranceThreshold) {

                    float extraSeverance = highestEffectCount * highestEffectCount * highestEffectCount * 1.2F + getDamageMultiplier(highestEffectCount);
                    modifierAbsoluteSeverance(enemy, attacker, weaponDamage, extraSeverance);


                    highestEffectCountMap.remove(enemyId);
                }
            }
        }
        return damage;
    }

    private float getDamageMultiplier(float effectCount) {
        return 1 + effectCount * 2147483648F;
    }

    private List<MobEffect> getRandomStatusEffects(int count) {
        if (harmfulEffectsCache == null) {
            harmfulEffectsCache = new ArrayList<>();
            for (ResourceLocation effectId : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
                if (effect != null && effect.getCategory() == MobEffectCategory.HARMFUL) {
                    harmfulEffectsCache.add(effect);
                }
            }
        }

        List<MobEffect> selectedEffects = new ArrayList<>();
        Random random = new Random();
        List<MobEffect> tempEffects = new ArrayList<>(harmfulEffectsCache);

        while (selectedEffects.size() < count && !tempEffects.isEmpty()) {
            int index = random.nextInt(tempEffects.size());
            selectedEffects.add(tempEffects.get(index));
            tempEffects.remove(index);
        }
        return selectedEffects;
    }

    private static void applyStatusEffects(LivingEntity entity, List<MobEffect> statusEffects, int level, UUID entityId) {

        Set<MobEffect> entityEffects = appliedEffectsMap.computeIfAbsent(entityId, k -> new HashSet<>());



        for (MobEffect effect : statusEffects) {

            entityEffects.add(effect);


            MobEffectInstance instance = new MobEffectInstance(effect, level * 20 * 100, 4);

            try {

                LivingEntityUtil.forceAddEffect(entity, instance);
            } catch (Exception e) {

                entity.addEffect(instance);
            }
        }
    }

    private int getHighestEffectCount(UUID entityId, int currentCount) {
        Integer highestCount = highestEffectCountMap.get(entityId);
        if (highestCount == null || currentCount > highestCount) {
            highestEffectCountMap.put(entityId, currentCount);
            return currentCount;
        }
        return highestCount;
    }
}




