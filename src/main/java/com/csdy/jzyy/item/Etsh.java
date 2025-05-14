package com.csdy.jzyy.item;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.item.register.ItemRegister;
import com.csdy.jzyy.modifier.util.font.ReFont;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

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
        System.out.println("玩家最大生命值"+player.getMaxHealth());
        InteractionHand hand = event.getHand();
        ItemStack heldItem = player.getItemInHand(hand);
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Direction face = event.getFace();

        if (world.isClientSide()) return;
        if (!heldItem.is(ItemRegister.ETSH.get())) return;
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
            // Place the 3x3 end portal frame
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos placePos = centerPos.offset(x, 0, z);
                    world.setBlockAndUpdate(placePos, Blocks.END_PORTAL.defaultBlockState());
                }
            }

            // Play end portal opening sound at the center position
            world.playSound(null, centerPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);

            // Consume the item if not in creative mode
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        } else {
            player.displayClientMessage(Component.literal("没有足够的空间放置3x3末地传送门。"), true);
        }
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @javax.annotation.Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return ReFont.getFont();
            }
        });
    }
}
