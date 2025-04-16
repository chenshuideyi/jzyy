package com.csdy.jzyy.modifier.modifier.yue_zheng_ling;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.modifier.DiademaModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
//@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Cumulonimbus extends NoLevelsModifier implements EquipmentChangeModifierHook, SlotStackModifierHook {


    @Override
    public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
        Level level = context.getLevel();
        LivingEntity holder = context.getEntity();
        if ((context.getLevel() instanceof ServerLevel serverLevel)) setRain(serverLevel);
//        else {
//            level.playSound(holder, holder.blockPosition, JzyySoundsRegister.CUMULONIMBUS.get(), SoundSource.MUSIC, 1.0F, 1.0F);
//        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(IToolStackView tool, ModifierEntry entry, ItemStack held, Slot slot, Player player, SlotAccess access) {
        player.playSound(JzyySoundsRegister.CUMULONIMBUS.get(),1,1);
        return false;
    }

//    @Override
//    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
//        Level level = context.getLevel();
//        LivingEntity holder = context.getEntity();
//        if (!(context.getLevel() instanceof ServerLevel serverLevel)) return;
//        if (holder instanceof ServerPlayer serverPlayer) {
//            // 停止特定 SoundEvent
//            serverPlayer.connection.send(
//                    new ClientboundStopSoundPacket(
//                            JzyySoundsRegister.CUMULONIMBUS.get().getLocation(), // SoundEvent 的 ResourceLocation
//                            SoundSource.MUSIC // 必须和播放时的 SoundSource 一致
//                    )
//            );
////            isPlaying = false;
//        }
//    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        hookBuilder.addHook(this, ModifierHooks.SLOT_STACK);
    }


    public static void setRain(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        //好几把麻烦
        serverLevel.setWeatherParameters(
                0, // clearTime
                240*20+20*46,  // ticks
                true,      // 下雨
                false      // 雷暴
        );
    }

}
