package com.csdy.jzyy.item.food;

import com.csdy.jzyy.modifier.util.font.RainbowFont;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CsdySword extends ItemGenericFood {

    public CsdySword() {
        super(16384, 16384, false, true, true, 1);
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        super.finishUsingItem(stack, worldIn, livingEntity);
        return stack;
    }
    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal(("桃子味雪糕"));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(Component.literal("她在喷屎!"));
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return RainbowFont.getFont();
            }
        });
    }
}
