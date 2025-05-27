package com.csdy.jzyy.item.fake;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.item.register.HideRegister;
import com.csdy.jzyy.modifier.util.font.NullFont;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus =  Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FakeItem extends Item {
    public FakeItem() {
        super(new Item.Properties());
    }

    public void releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
        super.releaseUsing(p_41412_, p_41413_, p_41414_, p_41415_);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }

    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        return super.getDefaultTooltipHideFlags(stack);
    }

    public Component getName(ItemStack p_41458_) {
        return super.getName(p_41458_);
    }

    public int getBarColor(ItemStack p_150901_) {
        return super.getBarColor(p_150901_);
    }

    protected String getOrCreateDescriptionId() {
        return super.getOrCreateDescriptionId();
    }

    public int getBarWidth(ItemStack p_150900_) {
        return super.getBarWidth(p_150900_);
    }

    public Rarity getRarity(ItemStack p_41461_) {
        return super.getRarity(p_41461_);
    }

    public float getDestroySpeed(ItemStack p_41425_, BlockState p_41426_) {
        return super.getDestroySpeed(p_41425_, p_41426_);
    }

    public boolean hurtEnemy(ItemStack p_41395_, LivingEntity p_41396_, LivingEntity p_41397_) {
        return super.hurtEnemy(p_41395_, p_41396_, p_41397_);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return super.onLeftClickEntity(stack, player, entity);
    }

    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.literal("「有时 有时 摔了一跤、」"));
        list.add(Component.literal("好冷 好冷 好冷 好冷 好冷"));
        list.add(Component.literal("不要向我搭话"));
        list.add(Component.literal("你的 你的 你的声音"));
        list.add(Component.literal("好远 好远 好远 好远 好远"));
        list.add(Component.literal("不要伤害我"));
        list.add(Component.literal("追逐着遥远的梦想？"));
        list.add(Component.literal("好快 好快 好快 好快？"));
        list.add(Component.literal("追不上啊？"));
        list.add(Component.literal("无法全部舍弃掉的残存思念"));
        list.add(Component.literal("可恨 可恨 可恨 可恨 可恨"));
        list.add(Component.literal("不会被原谅吗？"));
        list.add(Component.literal("应该 应该做着梦的"));
        list.add(Component.literal("好可怕 好可怕 好可怕 好可怕"));
        list.add(Component.literal("不要靠近我"));
        list.add(Component.literal("求求你了"));
        list.add(Component.literal("无论第几次 无论第几次"));
        list.add(Component.literal("为了让我作为我存在"));
        list.add(Component.literal("此物品已被CSDY施加伟大封印"));
        super.appendHoverText(stack, level, list, flag);
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @javax.annotation.Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return NullFont.getFont();
            }
        });
    }

    @SubscribeEvent
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event) { // 改为 static
        // 添加一个基本的非空检查，更安全
        event.getItemStack();
        if (event.getItemStack().isEmpty()) {
            return;
        }

        // 使用直接比较 == 可能更优，假设 HideRegister.FAKE_ITEM 是 RegistryObject<Item>
        if (event.getItemStack().getItem() == HideRegister.FAKE_ITEM.get()) {
            int lightGray = 0xFFC0C0C0; // ARGB 格式
            int darkerGray = 0xFFA8A8A8; // ARGB 格式

            event.setBackgroundStart(lightGray);
            event.setBackgroundEnd(lightGray); // 相同颜色 = 实心背景
            event.setBorderStart(darkerGray);
            event.setBorderEnd(darkerGray);   // 相同颜色 = 实心边框
        }
    }

    public Item asItem() {
        return HideRegister.FAKE_ITEM.get();
    }
}
