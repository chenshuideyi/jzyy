package com.csdy.modifier.diadema;

import com.csdy.DiademaModifier;
import com.csdy.diadema.DiademaRegister;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.FollowDiademaMovement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Gula extends DiademaModifier implements EquipmentChangeModifierHook {
    private Diadema gula;
    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (entity instanceof Player player) {
            if (context.getLevel() instanceof ServerLevel serverLevel) {
                if (context.getChangedSlot().isArmor()) {
                this.gula = DiademaRegister.GULA.get().CreateInstance(new FollowDiademaMovement(player));
                }
            }
        }
    }

    @Override
     public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (entity instanceof Player) {
            if (context.getChangedSlot().isArmor() && gula!=null) {
                this.gula.kill();
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        super.registerHooks(hookBuilder);
    }

    @Override
    protected DiademaType getDiademaType() {
        return DiademaRegister.GULA.get();
    }

}
