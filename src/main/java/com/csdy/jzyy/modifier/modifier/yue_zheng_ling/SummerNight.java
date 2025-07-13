package com.csdy.jzyy.modifier.modifier.yue_zheng_ling;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SummerNight extends NoLevelsModifier implements MeleeDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        return calculateSeasonalValue(damage);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    public static float calculateSeasonalValue(float baseValue) {
        ZoneId chinaZone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime now = ZonedDateTime.now(chinaZone);

        boolean isSummer = isSummer(now);
        boolean isNight = isNight(now);

        float result = baseValue;
        if (isSummer) {
            result *= 2;
        }
        if (isNight) {
            result *= 2;
        }

        return result;
    }

    private static boolean isSummer(ZonedDateTime date) {
        int month = date.getMonthValue();
        return month >= 6 && month <= 8;
    }

    private static boolean isNight(ZonedDateTime date) {
        int hour = date.getHour();
        return hour >= 20 || hour < 6;
    }

}
