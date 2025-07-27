package com.csdy.jzyy.item;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.item.register.ItemRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID)
public class PortableEndPortal extends Item {
    public PortableEndPortal() {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.protable_end_portal.line1").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
    }

    @SubscribeEvent
    public static void onPlayerInteractBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack heldItem = player.getItemInHand(hand);
        Level world = event.getLevel();
        BlockPos pos = event.getPos(); 
        Direction face = event.getFace();

        if (world.isClientSide()) return;

        if (!heldItem.is(ItemRegister.PORTABLE_END_PORTAL.get())) return;

        if (world.getBlockState(pos).isAir()) return;

        BlockPos centerPos = pos.relative(face);

        boolean canPlace = true;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = centerPos.offset(x, 0, z);
                if (!world.getBlockState(checkPos).isAir() && !world.getBlockState(checkPos).canBeReplaced()) {
                    canPlace = false;
                    break;
                }
            }
            if (!canPlace) break;
        }

        if (canPlace) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos placePos = centerPos.offset(x, 0, z);
                    world.setBlockAndUpdate(placePos, Blocks.END_PORTAL.defaultBlockState());
                }
            }

            world.playSound(null, centerPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        } else {
            player.displayClientMessage(Component.literal("§c需要 3x3 的空位才能放置末地传送门！"), true);
        }
    }



}
