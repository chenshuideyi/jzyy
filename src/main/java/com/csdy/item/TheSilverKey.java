package com.csdy.item;

import com.csdy.item.util.RandomTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TheSilverKey extends Item {

    public TheSilverKey() {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    private List<String> getLoadedDimensions() {
        // 获取所有已加载维度
        return Minecraft.getInstance().level.getServer().levelKeys()
                .stream().map(ResourceKey::location)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }



    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(world, player, hand);//能使用物品的一定是玩家 所以改成player
        if (player instanceof ServerPlayer) RandomTeleporter.teleportPlayerToRandomDimension((ServerPlayer) player);
        return use;
    }


}
