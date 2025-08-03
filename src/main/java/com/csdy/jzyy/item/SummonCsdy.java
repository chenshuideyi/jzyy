package com.csdy.jzyy.item;

import com.csdy.jzyy.entity.JzyyEntityRegister;
import com.csdy.jzyy.entity.boss.entity.SwordManCsdy;
import com.csdy.jzyy.font.Rarity.ExtendedRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SummonCsdy extends Item {

    public SummonCsdy() {
        super((new Item.Properties()).stacksTo(1).rarity(ExtendedRarity.RAINBOW));
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 先调用父类的use方法（保持原有效果）
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);

        // 只在服务端执行生成逻辑（避免客户端也执行）
        if (!level.isClientSide()) {
            // 计算玩家头顶20格的位置
            BlockPos spawnPos = player.blockPosition().above(20);

            // 创建僵尸实体
            SwordManCsdy csdy = new SwordManCsdy(JzyyEntityRegister.SWORD_MAN_CSDY.get(), level);

            // 设置僵尸位置
            csdy.moveTo(
                    spawnPos.getX() + 0.5, // 使用方块中心坐标
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    player.getYRot(),
                    0
            );

            // 将僵尸添加到世界中
            level.addFreshEntity(csdy);

        }

        return use;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.summon_csdy_line1").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
    }

}
