package com.csdy.jzyy.modifier.modifier.yue_zheng_ling;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Singer extends NoLevelsModifier implements MeleeDamageModifierHook {
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float baseDamage, float damage) {
        int currentVolume = getCurrentVolume();
        return damage * (currentVolume * 0.02f);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    public interface WinMM extends Library {
        WinMM INSTANCE = Native.load("winmm", WinMM.class);
        int waveOutGetVolume(Pointer hwo, IntByReference dwVolume);
    }

    public static int getCurrentVolume() {
        IntByReference volumeRef = new IntByReference();
        WinMM.INSTANCE.waveOutGetVolume(null, volumeRef);
        int volume = volumeRef.getValue();

        // 提取左右声道的音量值
        int leftVolume = volume & 0xFFFF;      // 低 16 位
        int rightVolume = (volume >> 16) & 0xFFFF; // 高 16 位

        // 计算左右声道的平均值
        int averageVolume = (leftVolume + rightVolume) / 2;

        // 将音量值转换为百分比（0xFFFF 对应 100%）
        int volumePercentage = (int) ((averageVolume / (double) 0xFFFF) * 100);

        return volumePercentage;
    }

}
