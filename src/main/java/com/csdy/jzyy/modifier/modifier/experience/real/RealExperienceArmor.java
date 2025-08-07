package com.csdy.jzyy.modifier.modifier.experience.real;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RealExperienceArmor extends NoLevelsModifier implements DamageBlockModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource source, float damage) {
        if (!(context.getEntity() instanceof Player player)) return false;
        if (damage < player.experienceLevel * 2) {
            int levelsToConsume = (int) damage;
            player.giveExperienceLevels(-levelsToConsume /2);

            spawnExperienceOrbs(player, levelsToConsume / 2);
            return true;
        }
        return false;
    }


    private void spawnExperienceOrbs(Player player, int levelsToConvert) {
        if (levelsToConvert <= 0) return;

        // 将经验等级转换为经验值（使用Minecraft的经验计算公式）
        int experience = levelsToExperience(levelsToConvert);

        // 在世界中生成经验球
        Level level = player.level;
        if (!level.isClientSide) {
            // 将总经验分成多个经验球（每个经验球最多包含一定数量的经验）
            while (experience > 0) {
                int orbValue = experience;
                ExperienceOrb expOrb = new ExperienceOrb(
                        level,
                        player.getX(),
                        player.getY() + player.getEyeHeight(),
                        player.getZ(),
                        orbValue
                );

                // 给经验球一些初速度
                expOrb.setDeltaMovement(
                        (level.random.nextDouble() - 0.5) * 0.5,
                        level.random.nextDouble() * 0.5,
                        (level.random.nextDouble() - 0.5) * 0.5
                );

                level.addFreshEntity(expOrb);
                experience -= orbValue;
            }
        }
    }

    // 将经验等级转换为经验值
    private int levelsToExperience(int levels) {
        // 这是Minecraft中经验等级到经验值的转换公式
        if (levels <= 16) {
            return levels * levels + 6 * levels;
        } else if (levels <= 31) {
            return (int) (2.5 * levels * levels - 40.5 * levels + 360);
        } else {
            return (int) (4.5 * levels * levels - 162.5 * levels + 2220);
        }
    }

}
