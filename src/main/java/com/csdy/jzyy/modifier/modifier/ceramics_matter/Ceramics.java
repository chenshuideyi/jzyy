package com.csdy.jzyy.modifier.modifier.ceramics_matter;


import com.c2h6s.etstlib.entity.specialDamageSources.LegacyDamageSource;
import com.c2h6s.etstlib.entity.specialDamageSources.PercentageBypassArmorSource;
import com.csdy.jzyy.modifier.register.JzyyModifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

public class Ceramics extends JzyyModifier {


    public int getModifierLevel(ModifierEntry entry){
        return Math.min(entry.getLevel(), 5);
    }

    @Override
    public LegacyDamageSource modifyDamageSource(IToolStackView tool, ModifierEntry entry, LivingEntity attacker, InteractionHand hand, Entity target, EquipmentSlot sourceSlot, boolean isFullyCharged, boolean isExtraAttack, boolean isCritical, LegacyDamageSource source){
        if (!(attacker instanceof Player player)) return source;
        return PercentageBypassArmorSource.playerAttack(player,100);
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        volatileData.addSlots(SlotType.UPGRADE, 15 * getModifierLevel(modifier));
        volatileData.addSlots(SlotType.ABILITY, 15 * getModifierLevel(modifier));
        volatileData.addSlots(SlotType.DEFENSE, 15 * getModifierLevel(modifier));
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        if (target == null) return baseDamage;
        if (attacker instanceof Player player) {
            return damage * getModifierLevel(entry) * 12F * 20F;
        }
        return damage;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry entry, EquipmentContext context,
                                   EquipmentSlot slot, DamageSource source, float damage) {
        return damage <= 10000000 * getModifierLevel(entry);
    }


    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        var entity = context.getEntity();
        if (entity == null) return amount;

        return amount * (1.0f - getModifierLevel(entry) * 0.24f);
    }


}
