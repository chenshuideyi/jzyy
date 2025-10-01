package com.csdy.jzyy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TianyuanBox extends Item {
    public TianyuanBox() {
        super((new Properties()).stacksTo(1).rarity(Rarity.EPIC));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem()))return InteractionResultHolder.fail(stack);
        return InteractionResultHolder.consume(stack);
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 1;
    }
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BLOCK;
    }


    public Map<ResourceLocation, ToolDefinition> getTools() {
        return ToolDefinitionLoader.getInstance()
                .getRegisteredToolDefinitions()
                .stream()
                .collect(Collectors.toMap(ToolDefinition::getId, Function.identity()));
    }
    private final Random RANDOM = new Random();
    public ToolDefinition getRandomToolDefinition() {
        Map<ResourceLocation, ToolDefinition> tools = getTools();
        if (tools.isEmpty()) {
            throw new IllegalStateException("No tool definitions available for mod " );
        }
        List<ToolDefinition> toolList = new ArrayList<>(tools.values());
        return toolList.get(RANDOM.nextInt(toolList.size()));
    }
    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        ItemStack retval = super.finishUsingItem(stack, level, living);
        if (living instanceof Player player) {
            ToolStack tools = ToolBuildHandler.buildToolRandomMaterials((IModifiable) ForgeRegistries.ITEMS.getValue(getRandomToolDefinition().getId()),RandomSource.create());
            ItemEntity entity=new ItemEntity(level,player.getX(),player.getY(),player.getZ(),tools.createStack());
            entity.setGlowingTag(true);
            level.addFreshEntity(entity);
            stack.setCount(stack.getCount()-1);
            player.getCooldowns().addCooldown(stack.getItem(),20);
        }
        return retval;
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.jzyy.tianyuan_box.line1").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
    }

}
