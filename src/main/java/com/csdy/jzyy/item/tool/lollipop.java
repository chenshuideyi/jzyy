package com.csdy.jzyy.item.tool;


import com.csdy.jzyy.item.tool.until.ToolDefinitions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.Iterator;
import java.util.List;

import static slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook.KEY_DRAWTIME;


//public class lollipop extends ModifiableItem {
//    public lollipop(Properties properties) {
//        super(properties, ToolDefinitions.LOLLIPOP_TD);
//    }
//    @Override
//    public boolean mineBlock(ItemStack p_41416_, Level p_41417_, BlockState p_41418_, BlockPos p_41419_, LivingEntity p_41420_) {
//        return true;
//    }
//
//    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
//        ToolStack tool=ToolStack.from(stack);
//        return tool.getStats().get(ToolStats.MINING_SPEED);
//    }
//
//    @Override
//    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
//        return true;
//    }
//
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        ItemStack stack = player.getItemInHand(hand);
//        ToolStack tool = ToolStack.from(stack);
//        tool.getPersistentData().putInt(KEY_DRAWTIME,32);
//        player.startUsingItem(hand);
//        if (tool.isBroken()){
//            return InteractionResultHolder.fail(stack);
//        }else if (!tool.isBroken()) {
//            return InteractionResultHolder.pass(stack);
//        }
//        return InteractionResultHolder.consume(stack);
//    }
//
//    @Override
//    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
////        ScopeModifier.stopScoping(livingEntity);
//        ToolStack tool = ToolStack.from(stack);
//        if (tool.isBroken()){
//            tool.getPersistentData().remove(KEY_DRAWTIME);
//            return stack;
//        }
//        if (!tool.isBroken()) {
//            eat(tool, livingEntity);
//            if (livingEntity instanceof ServerPlayer player) {
//                player.awardStat(Stats.ITEM_USED.get(this));
//                ToolDamageUtil.damageAnimated(tool, 1, player);
//                tool.getPersistentData().remove(KEY_DRAWTIME);
//            }
//        }
//        return stack;
//    }
//
//    private static final Lazy<ItemStack> BACON_STACK = Lazy.of(() -> new ItemStack(TinkerCommons.bacon));
//    private void eat(IToolStackView tool, LivingEntity entity) {
//        if (entity instanceof Player player && player.canEat(false)) {
//            Level world = entity.level();
//            int a = 6;
//            //吃完加饱食度
//            player.getFoodData().eat(a, a*0.5f);
//            ModifierUtil.foodConsumer.onConsume(player, BACON_STACK.get(), a, 0.6F);
//            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
//            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
//            //扣耐久
//            if (ToolDamageUtil.directDamage(tool, a*3 , player, player.getUseItem())) {
//                player.broadcastBreakEvent(player.getUsedItemHand());
//            }
//        }
//    }
//
//    public int getUseDuration(ItemStack stack) {
//        return 32;
//    }
//    @Override
//    public UseAnim getUseAnimation(ItemStack stack) {
//        return BlockingModifier.blockWhileCharging(ToolStack.from(stack), UseAnim.EAT);
//    }
//    public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
//        tooltips = this.getTooltipStats(tool, player, tooltips, key, tooltipFlag);
//        return tooltips;
//    }
//    public List<Component> getTooltipStats(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
//        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
//        if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
//            builder.add(ToolStats.DURABILITY);
//        }
//        if (tool.hasTag(TinkerTags.Items.MELEE)) {
//            builder.add(ToolStats.ATTACK_DAMAGE);
//            builder.add(ToolStats.ATTACK_SPEED);
//        }
//        if (tool.hasTag(TinkerTags.Items.HARVEST)) {
//            builder.add(ToolStats.HARVEST_TIER);
//            builder.add(ToolStats.MINING_SPEED);
//        }
//        builder.addAllFreeSlots();
//        Iterator var7 = tool.getModifierList().iterator();
//        while(var7.hasNext()) {
//            ModifierEntry entry = (ModifierEntry)var7.next();
//            entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltips, key, tooltipFlag);
//        }
//        return tooltips;
//    }
//}
