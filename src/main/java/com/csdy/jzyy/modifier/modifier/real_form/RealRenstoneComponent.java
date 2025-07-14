package com.csdy.jzyy.modifier.modifier.real_form;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class RealRenstoneComponent extends RealFormBaseModifier {

    public RealRenstoneComponent(String materialId, MaterialVariantId reMaterialId , String text) {
        super(materialId, reMaterialId, text);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) return false;

        // 计算周围15格内红石信号总量
        int totalRedstonePower = calculateSurroundingRedstonePower(holder.level(), holder.blockPosition());

        // 存储状态用于缓存
        boolean isActive = totalRedstonePower > 200;

        return isActive;
    }

    // 红石信号检测方法（带性能优化）
    private int calculateSurroundingRedstonePower(Level level, BlockPos center) {
        int radius = 15; // 检测半径
        int totalPower = 0;

        // 只检测Y层±3范围（红石通常在同一平面）
        for (int y = -3; y <= 3; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);

                    // 快速跳过空气方块
                    if (level.isEmptyBlock(pos)) continue;

                    // 获取红石信号（包括直接强度和间接充能）
                    int power = Math.max(
                            level.getDirectSignal(pos, Direction.UP),
                            level.getDirectSignalTo(pos)
                    );

                    // 特殊检测红石块和充能方块
                    if (power == 0 && level.getBlockState(pos).is(Blocks.REDSTONE_BLOCK)) {
                        power = 15;
                    }

                    totalPower += power;

                    // 提前终止检测（如果已经超过阈值）
                    if (totalPower > 250) return totalPower;
                }
            }
        }
        return totalPower;
    }

}
