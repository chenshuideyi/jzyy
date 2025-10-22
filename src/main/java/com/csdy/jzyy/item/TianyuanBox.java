package com.csdy.jzyy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.*;
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

    private RandomMaterial getRandomMaterial(ItemStack stack) {
        CompoundTag c = stack.getOrCreateTag();
        String s = "TianYuanTier";
        int a = c.getInt(s);
        return getInitialRange(a).build();
    }

    private RandomMaterial.RandomBuilder getInitialRange(int tier) {
        if (tier >= 3 && tier <= 5) {
            return RandomMaterial.random().minTier(tier - 2).maxTier(tier);
        } else if (tier == 6) {
            return RandomMaterial.random().minTier(tier).maxTier(tier + 2);
        } else if (tier == 7) {
            return RandomMaterial.random().minTier(tier + 1).maxTier(tier + 4);
        } else if (tier == 8) {
            return RandomMaterial.random().minTier(tier + 2).maxTier(tier + 8);
        } else if (tier == 9) {
            return RandomMaterial.random().minTier(tier + 8).maxTier(Integer.MAX_VALUE);
        }
        return RandomMaterial.random().maxTier(1);
    }
    private int getMinTier(int tier) {
        if (tier >= 3 && tier <= 5) {
            return tier - 2;
        } else if (tier >= 6&&tier<=9) {
            int a;
            switch (tier){
                case 7-> a=tier+1;
                case 8-> a=tier+2;
                case 9-> a=tier+8;
                default -> a=tier;
            }
            return a;
        }
        return 1;
    }
    private int getMaxTier(int tier) {
        if (tier >= 3 && tier <= 5) {
            return tier;
        } else if (tier >= 6&&tier<=8) {
            int a;
            switch (tier){
                case 7-> a=tier+4;
                case 8-> a=tier+8;
                default -> a=tier+2;
            }
            return a;
        }
        return Integer.MAX_VALUE;
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
    public ToolStack buildTools(Item item,ToolDefinition definition, MaterialNBT materials) {
        return ToolStack.createTool(item, definition, materials);
    }
    private ToolStack getRandomTools(ItemStack stack) {
        ToolDefinition definition = getRandomToolDefinition();
        Item item=ForgeRegistries.ITEMS.getValue(definition.getId());
        List<MaterialStatsId> stats = ToolMaterialHook.stats(definition);
        return buildTools(item, definition, RandomMaterial.build(stats, Collections.nCopies(stats.size(), getRandomMaterial(stack)), RandomSource.create()));
    }
    public ToolStack getToolStack(ItemStack stack) {
        ToolStack resultStack = null;
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            resultStack = getRandomTools(stack);
            if (isValidToolStack(resultStack)) {
                break;
            }
        }
        return resultStack;
    }
    private boolean isValidToolStack(ToolStack toolStack) {
        if (toolStack == null) return false;
        if (!toolStack.hasTag(TinkerTags.Items.MULTIPART_TOOL)) return false;
        ItemStack itemStack = toolStack.createStack();
        return !itemStack.isEmpty() && itemStack.getItem() != Items.AIR;
    }
    private int getRandomInt(int a) {
        if (a >= 32 && a <= 56) {
            return 4;
        } else if (a >= 56 && a <= 74) {
            return 5;
        } else if (a >= 74 && a <= 86) {
            return 6;
        } else if (a >= 86 && a <= 94) {
            return 7;
        } else if (a >= 94 && a <= 98) {
            return 8;
        } else if (a >= 98 && a <= 100) {
            return 9;
        }
        return 3;
    }
    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        ItemStack retval = super.finishUsingItem(stack, level, living);
        if (living instanceof Player player) {
            CompoundTag c = stack.getOrCreateTag();
            String s = "TianYuanTier";
            Random random=new Random();
            if (c.getInt(s)==0) {
                c.putInt(s,getRandomInt(random.nextInt(101)));
            }else
            if (c.getInt(s)>=3) {
                ToolStack tools = getToolStack(stack);
                ItemEntity entity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), tools.createStack());
                entity.setGlowingTag(true);
                level.addFreshEntity(entity);
                stack.setCount(stack.getCount() - 1);
            }
            player.getCooldowns().addCooldown(stack.getItem(),20);
        }
        return retval;
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        String[] array = new String[]{"msg.tianyuan_box1", "msg.tianyuan_box2", "msg.tianyuan_box3", "msg.tianyuan_box4", "msg.tianyuan_box5", "msg.tianyuan_box6", "msg.tianyuan_box7"};
        CompoundTag c = stack.getOrCreateTag();
        String s = "TianYuanTier";
        int a = c.getInt(s);
        if (a!=0) {
            tooltip.add(Component.translatable(array[a-3]).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
            tooltip.add(Component.translatable("item.jzyy.tianyuan_box.line1").append(getMinTier(a)+"—").append(getMaxTier(a)+"").append(Component.translatable("item.jzyy.tianyuan_box.line2")).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
        }
        tooltip.add(Component.translatable("item.jzyy.tianyuan_box.line3").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));

    }

}
