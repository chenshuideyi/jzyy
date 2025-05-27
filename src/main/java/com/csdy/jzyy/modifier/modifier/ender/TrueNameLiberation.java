package com.csdy.jzyy.modifier.modifier.ender;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TrueNameLiberation extends RealFormBaseModifier {
    private final static String TRUE_NAME = "誓约胜利之剑";

    public TrueNameLiberation(String materialId, MaterialVariantId reMaterialId, String text) {
        super(materialId, reMaterialId, text);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof Player)) {
            return false;
        }

        // 正确获取工具名称的字符串表示
        Component nameComponent = tool.createStack().getHoverName();
        String toolName = nameComponent.getString();
        System.out.println(toolName);
        return isTrueName(toolName);
    }

    private boolean isTrueName(String toolName) {
        // 检查工具名是否包含真名
        return toolName.toLowerCase().contains(TRUE_NAME.toLowerCase());
    }
}
