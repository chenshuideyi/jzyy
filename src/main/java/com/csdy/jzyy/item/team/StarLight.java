package com.csdy.jzyy.item.team;

import com.csdy.jzyy.modifier.util.font.DcFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static com.csdy.jzyy.JzyyModMain.toolTip;


public class StarLight extends Item {

    private final Minecraft mc = Minecraft.getInstance();

    public StarLight() {
        super((new Item.Properties()).stacksTo(1));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        switch (toolTip) {
            case 1:
                list.add(Component.literal(""));
                list.add(Component.literal("见星海无岸，证宇宙无边，我们一路同行，去往梦的彼方                       "));
                break;
            case 2:
                list.add(Component.literal(""));
                list.add(Component.literal("我们，在那属于传奇的时间线上，经历春夏秋冬...                          "));
                break;
            case 3:
                list.add(Component.literal(""));
                list.add(Component.literal("她，                                                            "));
                list.add(Component.literal("诞生于时间之初，"));
                list.add(Component.literal("穿梭于维度之外，"));
                list.add(Component.literal("泛着神秘的辉光....."));
                break;
            case 4:
                list.add(Component.literal(""));
                list.add(Component.literal("那飘零于时间的故事                                                 "));
                list.add(Component.literal("如是初源，如是终焉"));
                list.add(Component.literal("分叉的起点，终将在结局交汇"));
                list.add(Component.literal("那从往世荫蔽中破土的未来"));
                list.add(Component.literal("如炬如光"));
                list.add(Component.literal("繁花谢世之时，万物自此新生..."));
        }

        list.add(Component.literal(""));
        list.add(Component.literal("在手中时:"));
        list.add(Component.literal(" 没有 宇宙无边 力量"));
        list.add(Component.literal(" 没有 Terminal 攻击速度"));
        list.add(Component.literal(""));
        list.add(Component.literal("在心中时:"));
        list.add(Component.literal(" 没有 逐梦星光 守护"));
        list.add(Component.literal(" 没有 传奇 所向披靡"));
        list.add(Component.literal(""));

        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal(("[逐梦星光] ———— 仿制品"));
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @javax.annotation.Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return DcFont.getFont();
            }
        });
    }
}
