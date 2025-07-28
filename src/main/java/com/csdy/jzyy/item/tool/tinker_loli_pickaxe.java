package com.csdy.jzyy.item.tool;


import com.csdy.jzyy.item.tool.until.ToolDefinitions;
import com.csdy.tcondiadema.sounds.SoundsRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Iterator;
import java.util.List;


public class tinker_loli_pickaxe extends ModifiableItem {
    public tinker_loli_pickaxe(Properties properties) {
        super(properties, ToolDefinitions.TINKER_LOLI_PICKAXE_TD);
    }

    private static final double range = 100;

    @Override
    public boolean mineBlock(ItemStack p_41416_, Level p_41417_, BlockState p_41418_, BlockPos p_41419_, LivingEntity p_41420_) {
        return true;
    }

    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        ToolStack tool=ToolStack.from(stack);
        return tool.getStats().get(ToolStats.MINING_SPEED);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    public boolean canBeDepleted() {
        return false;
    }

    public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
        tooltips = this.getTooltipStats(tool, player, tooltips, key, tooltipFlag);
        return tooltips;
    }
    public List<Component> getTooltipStats(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);

        if (tool.hasTag(TinkerTags.Items.MELEE)) {
            builder.add(ToolStats.ATTACK_DAMAGE);
            builder.add(ToolStats.ATTACK_SPEED);
        }
        if (tool.hasTag(TinkerTags.Items.HARVEST)) {
            builder.add(ToolStats.HARVEST_TIER);
            builder.add(ToolStats.MINING_SPEED);
        }
        builder.addAllFreeSlots();
        Iterator var7 = tool.getModifierList().iterator();
        while(var7.hasNext()) {
            ModifierEntry entry = (ModifierEntry)var7.next();
            entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltips, key, tooltipFlag);
        }
        return tooltips;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        int entityCount = 0;
        int itemCount = 0;
        int xpCount = 0;
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);

        if (level instanceof ServerLevel serverLevel) {
            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range))) {
                if (entity != null && !(entity instanceof Player)) {
                    player.attack(entity);
                    entityCount++;
                }
            }

            for (ItemEntity item : serverLevel.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(range))) {
                if (item != null && !item.isRemoved()) {
                    item.moveTo(player.getX(), player.getY(), player.getZ());
                    itemCount++;
                }
            }

            // 吸引经验球部分
            for (ExperienceOrb xpOrb : serverLevel.getEntitiesOfClass(ExperienceOrb.class, player.getBoundingBox().inflate(range))) {
                if (xpOrb != null && !xpOrb.isRemoved()) {
                    xpOrb.moveTo(player.getX(), player.getY(), player.getZ());
                    xpCount++;
                }
            }

            player.displayClientMessage(Component.literal("已攻击" + entityCount + "个生物，吸引" + itemCount + "个物品和" + xpCount + "个经验球"), false);
        }

        player.playSound(SoundsRegister.LOLI_SUCCRSS.get(), 1, 1);
        return use;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }


}
