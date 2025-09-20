package com.csdy.jzyy.item.kill_count;

import com.csdy.jzyy.font.Rarity.ExtendedRarity;
import com.csdy.jzyy.item.fake.FakeItem;
import com.csdy.jzyy.item.fake.FakeStack;
import com.csdy.jzyy.ms.util.Helper;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseKillCountCounter extends Item {

    private final Rarity rarity;
    private final int killCount;

    public BaseKillCountCounter(Rarity rarity,int killCount) {
        super((new Item.Properties()).stacksTo(64).rarity(rarity));
        this.killCount = killCount;
        this.rarity = rarity;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.kill_count_counter.line1").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);
        ItemStack thisStack = player.getItemInHand(hand);
        if (player.level.isClientSide) {
            player.playSound(SoundEvents.PLAYER_LEVELUP);
        }
        if (level instanceof ServerLevel serverLevel) {
            if (!player.getAbilities().instabuild) {
                thisStack.shrink(1);
            }
            MinecraftServer server = serverLevel.getServer();
            for (Player spPlayer : server.getPlayerList().getPlayers()) {
                for (ItemStack stack : spPlayer.getInventory().items) {
                    if (stack.isEmpty() || !stack.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
                        continue;
                    }
                    stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                        state.setKillCount(state.getKillCount() + killCount);
                        stack.getOrCreateTag().put("bladeState", state.serializeNBT());
                    });
                }
            }
        }
        return use;
    }


}
