package com.csdy.jzyy.item.fake;

import com.csdy.jzyy.item.register.HideRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FakeStack extends ItemStack {
    public FakeStack(ItemLike like) {
        super(like);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return super.use(level, player, hand);
    }

    public @NotNull Component getDisplayName() {
        return super.getDisplayName();
    }



    public @NotNull Item getItem() {
        return HideRegister.FAKE_ITEM.get();
    }
}
