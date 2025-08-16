package com.csdy.jzyy.modifier.modifier.real_form;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

///归终真实形态——屠龙
///我将沐浴这份荣誉的光辉
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;

public class RealReturnToTheEnd extends RealFormBaseModifier {

    // 定义 "解放末地" 的进度ID（需替换为实际的进度ID）
    private static final ResourceLocation LIBERATE_END_ADVANCEMENT_ID =
            new ResourceLocation("minecraft", "end/kill_dragon");

    public RealReturnToTheEnd(String materialId, MaterialVariantId reMaterialId, String text) {
        super(materialId, reMaterialId, text);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof ServerPlayer player)) {
            return false;
        }

        return hasAdvancement(player, LIBERATE_END_ADVANCEMENT_ID);
    }

    private boolean hasAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(advancementId);
        if (advancement == null) {
            return false;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        return progress.isDone();
    }
}
