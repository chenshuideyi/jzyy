package com.csdy.jzyy.item;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.item.register.ItemRegister;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID)
public class SourceOfManaita extends Item {
    public SourceOfManaita() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.RARE));
    }

    @SubscribeEvent
    public static void onPlayerInteractBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack itemStack = event.getItemStack();

        // 检查玩家手中的物品是否是砧板之源
        if (!itemStack.getItem().equals(ItemRegister.SOURCE_OF_MANAITA.get())) {
            return;
        }

        // 只在服务端执行
        if (world.isClientSide()) {
            return;
        }

        // 防止事件被取消（比如某些保护插件）
        if (event.isCanceled()) {
            return;
        }

        BlockState blockState = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // 检查方块是否可挖掘（防止复制基岩等不可挖掘方块）
        if (blockState.getDestroySpeed(world, pos) < 0) {
            return;
        }

        // 检查方块是否是空气
        if (blockState.isAir()) {
            return;
        }

        // 获取方块的掉落物
        LootParams.Builder lootParams = new LootParams.Builder((ServerLevel) world)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, itemStack)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.BLOCK_STATE, blockState);

        if (blockEntity != null) {
            lootParams.withParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        }

        List<ItemStack> drops = blockState.getDrops(lootParams);

        // 如果方块没有掉落物，直接返回
        if (drops.isEmpty()) {
            return;
        }

        // 给玩家掉落物（每个掉落物都给64个）
        for (ItemStack drop : drops) {
            ItemStack copiedDrop = drop.copy();
            copiedDrop.setCount(64);

            // 尝试添加到玩家库存，如果满了就掉落在地上
            if (!player.getInventory().add(copiedDrop)) {
                player.drop(copiedDrop, false);
            }
        }

        itemStack.shrink(1);

        // 阻止后续的交互事件（比如防止打开箱子等）
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.source_of_manaita_line1")
                .withStyle(ChatFormatting.AQUA)
                .withStyle(ChatFormatting.ITALIC));
    }
}
