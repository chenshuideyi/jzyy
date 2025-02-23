package com.csdy.item;

import com.csdy.item.register.ItemRegister;
import com.csdy.until.Util;
import com.csdy.until.method.KillPlayerMethod;
import com.csdyms.test.EntitySlotAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class Death extends Item {

    public Death() {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }


    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(world, player, hand);//能使用物品的一定是玩家 所以改成player
//        Util.DeathPlayer((LocalPlayer) player);
//        DeadLists.isEntity(player);
        return use;
    }

    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(itemStack, world, entity, p_41407_, p_41408_);
        //KillPlayerMethod.csdykill(entity);
    }
}

