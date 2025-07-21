package com.csdy.jzyy.modifier.modifier;

import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class DeepSeaGirl extends NoLevelsModifier implements InventoryTickModifierHook, ModifyDamageModifierHook {

    @Override
    public void onInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (isCorrectSlot) {
            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER,37*20,0));
        }
    }

    ///感谢deepseek能让我两秒把这个写完
    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        // 获取攻击目标（玩家）
        LivingEntity target = context.getEntity();
        // 检查玩家是否在水里
        if (target.isInWater()) {
            // 获取玩家当前Y坐标
            double y = target.getY();

            // 设置减伤计算范围（示例：Y=60以下开始减伤，Y=0时达到最大减伤）
            double minY = 0;    // 最低Y值（最大减伤）
            double maxY = 60;   // 最高Y值（无减伤）

            // 计算减伤比例（Y越低，减伤越强）
            double scale = MathHelper.clamp((maxY - y) / (maxY - minY), 0.0, 1.0);

            // 设置减伤效果（例如：Y=0时减少50%伤害，Y=60时无减伤）
            float damageReduction = (float) (amount * scale * 0.5); // 0.5代表最多减伤50%
            return amount - damageReduction;
        }
        return amount;
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
    }
}
