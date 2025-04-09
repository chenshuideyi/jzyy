package com.csdy.jzyy.modifier.modifier;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Test extends NoLevelsModifier implements ToolStatsModifierHook, MeleeHitModifierHook {

    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        float rate = Float.MAX_VALUE;
        ToolStats.DRAW_SPEED.multiply(builder, 1 * rate);
        ToolStats.MINING_SPEED.multiply(builder, 1 * rate);
        ToolStats.DURABILITY.multiply(builder, 1 * rate);
        ToolStats.ATTACK_SPEED.multiply(builder, 1 * rate);
        ToolStats.ATTACK_DAMAGE.multiply(builder, 1 * rate);
        ToolStats.VELOCITY.multiply(builder, 1 * rate);
        ToolStats.ACCURACY.multiply(builder, 1 * rate);
        ToolStats.PROJECTILE_DAMAGE.multiply(builder, 1 * rate);
        ToolStats.ARMOR.multiply(builder, 1 * rate);
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, 1 * rate);
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {

    }





//    public static String getGPUName() {
//        try {
//            Process process = Runtime.getRuntime().exec("wmic path win32_VideoController get name");
//            process.waitFor();
//
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//
//            String line;
//            StringBuilder builder = new StringBuilder();
//            // 跳过第一行(标题行)
//            reader.readLine();
//            while ((line = reader.readLine()) != null) {
//                if (!line.trim().isEmpty()) {
//                    builder.append(line.trim()).append("\n");
//                }
//            }
//            return builder.toString().trim();
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return "无法获取显卡信息";
//        }
//    }

}
