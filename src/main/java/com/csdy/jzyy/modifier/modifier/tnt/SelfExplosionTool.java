package com.csdy.jzyy.modifier.modifier.tnt;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.SelfExplosion;

public class SelfExplosionTool extends Modifier implements InventoryTickModifierHook, ToolDamageModifierHook {

    @Override
    public int onDamageTool(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, int amount, @Nullable LivingEntity holder) {
        if (holder != null) {
            SelfExplosion(holder.level(),entry.getLevel(),holder);
            return amount * entry.getLevel();
        }
        return amount;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && holder.isAlive() && world.random.nextFloat() < 0.005f) {
            SelfExplosion(holder.level(),entry.getLevel(),holder);
            tool.setDamage(tool.getDamage()+entry.getLevel());
        }
    }



    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
