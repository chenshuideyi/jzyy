package com.csdy.jzyy.modifier.modifier.dx.tool;

import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BaseToolMusic extends NoLevelsModifier implements SlotStackModifierHook {
    private final SoundEvent  sound;

    public BaseToolMusic(SoundEvent sound) {
        this.sound = sound;
    }
    @Override
    public boolean overrideOtherStackedOnMe(IToolStackView tool, ModifierEntry entry, ItemStack held, Slot slot, Player player, SlotAccess access) {
        player.playSound(sound,1,1);
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.SLOT_STACK);
    }

}
