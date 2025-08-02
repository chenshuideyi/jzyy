package com.csdy.jzyy.item.food;

import com.csdy.jzyy.entity.JzyyEntityRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static com.csdy.jzyy.entity.monster.event.HJMSummon.trySpawnEntityNearPlayer;

public class LueMaoQu extends ItemGenericFood {
    public LueMaoQu() {
        super(2, 3, false, false, true, 64);
    }
    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
        super.finishUsingItem(stack, worldIn, livingEntity);
        var player = (Player) livingEntity;
        if (!player.level.isClientSide) {
            String[] array = new String[]{"msg.lue_mao_qu1", "msg.lue_mao_qu2", "msg.lue_mao_qu3", "msg.lue_mao_qu4", "msg.lue_mao_qu5"};
            Random random = new Random();
            int randomIndex = random.nextInt(array.length);
            int a = random.nextInt(9);
            if (a != 1) {
                player.sendSystemMessage(Component.translatable(array[randomIndex]));
            } else {
                player.sendSystemMessage(Component.translatable("msg.lue_mao_qu6"));
                trySpawnEntityNearPlayer(JzyyEntityRegister.HJM.get(),(ServerLevel) player.level, player, 1, 3, 6, MobEffects.GLOWING,300,0);
            }
        }
        return stack;
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(net.minecraft.network.chat.Component.translatable("item.jzyy.lue_mao_qu.tooltip1").withStyle(ChatFormatting.DARK_AQUA));
    }
}
