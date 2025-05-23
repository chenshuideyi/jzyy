package com.csdy.jzyy.modifier.modifier.pufferfish;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class QuillSpray extends Modifier implements OnAttackedModifierHook {

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                           EquipmentSlot slot, DamageSource source, float amount, boolean isDirectDamage) {

        LivingEntity holder = context.getEntity();
        Level level = holder.level();
        if (!level.isClientSide && isDirectDamage) {
            for (int i = 0; i < 14 * entry.getLevel(); i++) {
                Arrow arrow = new Arrow(level, holder);

                // 设置为中毒箭
                arrow.addEffect(new MobEffectInstance(
                        MobEffects.POISON,
                        100,
                        entry.getLevel()
                ));

                // 随机散射
                float spread = 120.0F; // 散射角度
                float xRot = holder.getXRot() + (level.random.nextFloat() - 0.5F) * spread;
                float yRot = holder.getYRot() + (level.random.nextFloat() - 0.5F) * spread;

                arrow.shootFromRotation(holder, xRot, yRot, 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(arrow);
            }

            level.playSound(null, holder.getX(), holder.getY(), holder.getZ(),
                    SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }
}
