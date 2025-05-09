package com.csdy.jzyy.modifier.modifier.real_form;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

///生命木真实形态——森林之力
///森林也是会反抗的！
public class RealLivingWood extends RealFormBaseModifier {

    public RealLivingWood(String materialId, MaterialVariantId reMaterialId , String text) {
        super(materialId, reMaterialId, text);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) {
            return false;
        }
        Level level = player.level();

        boolean isInNether = level.dimension() == Level.NETHER;

        boolean isInWater = player.isInWater() || isInWaterCauldron(player);

        return isInNether && isInWater;
    }

    private boolean isInWaterCauldron(Player player) {
        BlockPos pos = player.blockPosition();
        BlockState state = player.level().getBlockState(pos);

        if (state.getBlock() instanceof AbstractCauldronBlock cauldron) {
            if (cauldron instanceof LayeredCauldronBlock) {
                return state.getValue(LayeredCauldronBlock.LEVEL) > 0;
            }
        }
        return false;
    }


}
