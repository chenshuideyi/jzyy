package com.csdy.jzyy.modifier.modifier.eva;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.OnHoldingPreventDeathHook;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Cassius extends Modifier implements OnHoldingPreventDeathHook {

    @Override
    public float onHoldingPreventDeath(LivingEntity living, IToolStackView tool, ModifierEntry entry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource) {
        if (living instanceof Player player && !player.getCooldowns().isOnCooldown(tool.getItem())) {
            // 获取玩家等级
            int level = entry.getLevel();

            int baseCooldown = 20 * 200;

            double reducedCooldown = baseCooldown * Math.pow(0.25, level);

            int finalCooldown = Math.max(1, (int)reducedCooldown);

            player.getCooldowns().addCooldown(tool.getItem(), finalCooldown);

            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,12));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,200,12));

            return living.getMaxHealth();
        }
        return 0;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.PREVENT_DEATH);
    }
}
