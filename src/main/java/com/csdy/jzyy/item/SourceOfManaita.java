package com.csdy.jzyy.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class SourceOfManaita extends Item {
    public SourceOfManaita() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.RARE));
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        Level world = context.getLevel();
        Player player = context.getPlayer();

        if (!world.isClientSide()) {
            // 获取玩家看向的方块
            BlockPos targetPos = getTargetBlockPos(player);
            if (targetPos != null) {
                BlockState blockState = world.getBlockState(targetPos);
                BlockEntity blockEntity = world.getBlockEntity(targetPos);

                // 检查方块是否可挖掘（防止复制基岩等）
                if (blockState.getDestroySpeed(world, targetPos) >= 0) {
                    // 获取方块的掉落物
                    LootParams.Builder lootParams = new LootParams.Builder((ServerLevel) world)
                            .withParameter(LootContextParams.ORIGIN, player.position())
                            .withParameter(LootContextParams.TOOL, itemStack)
                            .withParameter(LootContextParams.THIS_ENTITY, player)
                            .withParameter(LootContextParams.BLOCK_STATE, blockState);

                    if (blockEntity != null) {
                        lootParams.withParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
                    }

                    List<ItemStack> drops = blockState.getDrops(lootParams);

                    // 给玩家掉落物
                    for (ItemStack drop : drops) {
                        // 复制64个
                        ItemStack copiedDrop = drop.copy();
                        copiedDrop.setCount(64);
                        player.getInventory().add(copiedDrop);
                    }

                    // 消耗一个物品（可选）
                    // itemStack.shrink(1);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    // 获取玩家看向的方块位置
    private BlockPos getTargetBlockPos(Player player) {
        var hitResult = player.pick(5.0D, 1.0F, false);
        if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return ((net.minecraft.world.phys.BlockHitResult) hitResult).getBlockPos();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.source_of_manaita_line1").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
    }

}
