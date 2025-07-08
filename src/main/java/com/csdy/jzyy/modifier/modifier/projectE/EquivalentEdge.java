package com.csdy.jzyy.modifier.modifier.projectE;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowDamageModifierHook;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.pow;

public class EquivalentEdge extends NoLevelsModifier implements MeleeDamageModifierHook, ArrowDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        if (!(context.getAttacker() instanceof ServerPlayer player)) return damage;

        return player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                .map(knowledge -> {
                    BigInteger emc = knowledge.getEmc();

                    double emcAsDouble = emc.doubleValue();
                    double bonusMultiplier = Math.log10(emcAsDouble / 1000.0 + 1); // +1避免log10(0)

//                    BigDecimal decimalValue = BigDecimal.valueOf(emcAsDouble);
//                    BigDecimal reducedValue = decimalValue.multiply(new BigDecimal("0.8"));
//                    BigInteger result = reducedValue.setScale(0, RoundingMode.HALF_UP).toBigInteger();
//
//                    knowledge.setEmc(result);
//                    knowledge.syncEmc(player);

                    // 计算最终伤害（保留原damage的加成）
                    return damage * (1.0f + (float) bonusMultiplier);
                })
                .orElse(damage); // 如果Capability未加载，返回原伤害
    }

    @Override
    public float getArrowDamage(ModDataNBT nbt, ModifierEntry entry, ModifierNBT modifierNBT, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull Entity target, float basedamage, float damage) {
        if (attacker instanceof ServerPlayer player && target instanceof LivingEntity) {
            return player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                    .map(knowledge -> {
                        BigInteger emc = knowledge.getEmc();

                        double emcAsDouble = emc.doubleValue();
                        double bonusMultiplier = Math.log10(emcAsDouble / 10000.0 + 1); // +1避免log10(0)

//                        BigDecimal decimalValue = BigDecimal.valueOf(emcAsDouble);
//                        BigDecimal reducedValue = decimalValue.multiply(new BigDecimal("0.8"));
//                        BigInteger result = reducedValue.setScale(0, RoundingMode.HALF_UP).toBigInteger();

//                        knowledge.setEmc(result);
//                        knowledge.syncEmc(player);

                        // 计算最终伤害（保留原damage的加成）
                        return damage * (1.0f + (float) bonusMultiplier);
                    })
                    .orElse(damage); // 如果Capability未加载，返回原伤害
        }
        return damage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_DAMAGE);
    }

}
