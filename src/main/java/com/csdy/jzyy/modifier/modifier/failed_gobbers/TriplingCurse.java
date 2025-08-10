package com.csdy.jzyy.modifier.modifier.failed_gobbers;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.font.RainbowText;
import com.csdy.jzyy.modifier.register.JzyyModifier;
import com.csdy.jzyy.modifier.util.CsdyModifierUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.*;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.text.DecimalFormat;
import java.util.List;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.OVERSLIME_FRIEND;

public class TriplingCurse extends JzyyModifier {
    public static final ResourceLocation triplingcursepoints = JzyyModMain.getResourceLoc("triplingcursepoints");

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifierEntry, Level level, LivingEntity entity, int index, boolean b, boolean b1, ItemStack itemStack) {
        int a=0;
        for (SlotType type:SlotType.getAllSlotTypes()) {
            a+=tool.getVolatileData().getSlots(type);
        }
        if (tool.getPersistentData().getInt(triplingcursepoints)!=a){
            tool.getPersistentData().putInt(triplingcursepoints,a);
            ToolStack.from(itemStack).rebuildStats();
        }
    }
    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        volatileData.addSlots(SlotType.UPGRADE, modifier.getLevel());
        volatileData.addSlots(SlotType.ABILITY, modifier.getLevel());
        volatileData.addSlots(SlotType.DEFENSE, modifier.getLevel());
        for (ModifierEntry entry : context.getModifierList()) {
            if (entry.getModifier() != modifier.getModifier()) {
                entry.getHook(ModifierHooks.VOLATILE_DATA).addVolatileData(context, modifier, volatileData);
            }
        }
    }
    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder){
        ModDataNBT a = (ModDataNBT) context.getPersistentData();
        int b =a.getInt(triplingcursepoints);
        double rate = Math.pow(1.33, b);
        if (b>0) {
            CsdyModifierUtil.multiplyAllToolStats(builder,rate);
        }
    }
    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifierEntry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (player != null) {
            int number = tool.getPersistentData().getInt(triplingcursepoints);
            if (number >= 17) {
                double rate = Math.pow(1.33, number);
                DecimalFormat df = new DecimalFormat("#.00");
                String formattedValue = df.format(rate);
                tooltip.add(this.applyStyle(Component.translatable(RainbowText.TriplingCurse("新的力量..."))));
                tooltip.add(this.applyStyle(Component.translatable(RainbowText.makeColour("呵呵...没想到这么强大"))));
                tooltip.add(this.applyStyle(Component.translatable(RainbowText.TriplingCurse("已获得全属性增益"))));
                tooltip.add(this.applyStyle(Component.translatable(RainbowText.makeColour2(formattedValue + "%"))));
            } else {
                tooltip.add(this.applyStyle((Component.translatable("仅凭这样的力量什么都做不到...")).append(number+"").withStyle(ChatFormatting.BLACK)));
            }
        }

    }
}
