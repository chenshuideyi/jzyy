package com.csdy.jzyy.modifier.modifier.ice_and_fire;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

//public class DragonKiller extends NoLevelsModifier implements MeleeDamageModifierHook {

//    @Override
//    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
//        LivingEntity target = context.getLivingTarget();
//        if (target instanceof EntityDragonBase dragon) {
//            DragonType type = dragon.dragonType;
//            if (type != null) {
//                return damage * 1.5f;
//            }
//
//        }
//        return damage;
//    }
//
//    @Override
//    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
//        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
//    }
//
//
//}
