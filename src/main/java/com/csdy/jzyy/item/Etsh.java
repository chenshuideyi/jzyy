package com.csdy.jzyy.item;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.item.register.ItemRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModMain.MODID)
public class Etsh extends Item {
    public Etsh() {
        super((new Item.Properties()).stacksTo(64).rarity(Rarity.EPIC));
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.etsh.line1").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC));
    }

    @SubscribeEvent
    public static void onPlayerInteractBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack heldItem = player.getItemInHand(hand);
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Direction face = event.getFace();
        if (!world.isClientSide()) {
            if (heldItem.is(ItemRegister.ETSH.get())) {
                if (!world.getBlockState(pos).isAir()) {
                    BlockPos placementPos = pos.relative(face);
                    if (world.getBlockState(placementPos).isAir() || world.getBlockState(placementPos).canBeReplaced()) {
                        world.setBlockAndUpdate(placementPos, Blocks.END_PORTAL.defaultBlockState());
                        event.setCanceled(true);
                        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                    } else {
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("无法在此放置末地传送门。"), true);
                    }
                }
            }
        }
    }
}
