package com.csdy.jzyy.item;

import com.csdy.jzyy.cheat.UI;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class InvokeCheatUI extends Item {
    public InvokeCheatUI() {
        super(new Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        UI.main(new String[]{});
        return super.use(p_41432_, p_41433_, p_41434_);
    }
}
