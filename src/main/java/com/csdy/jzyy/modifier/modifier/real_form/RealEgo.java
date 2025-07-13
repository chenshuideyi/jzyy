package com.csdy.jzyy.modifier.modifier.real_form;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

///EGO真实形态——纯真的自我
///“被理解者、被认可者和被追逐者的真实”
public class RealEgo extends RealFormBaseModifier {

    public RealEgo(String materialId, MaterialVariantId reMaterialId , String text) {
        super(materialId, reMaterialId,text);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) {
            return false;
        }
        String toolName = String.valueOf(tool.getItem().getName(tool.createStack()));
        return isName(player,toolName);
    }

    private boolean isName(Player player,String toolName){
        String playerName = String.valueOf(player.getName());
        return playerName.toLowerCase().contains(toolName.toLowerCase());
    }
}
