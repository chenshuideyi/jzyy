package com.csdy.jzyy.modifier.modifier.bian;

import com.jerotes.jerotes.effect.CorrosiveMobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class BitterRigidIce  {


    public static class slushing extends NoLevelsModifier implements EquipmentChangeModifierHook {


        private static final String IMMUNITY_KEY = "slushing_equipped";

        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
        }

        @Override
        public void onEquip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
                player.getPersistentData().putBoolean(IMMUNITY_KEY, true);
            }
        }

        @Override
        public void onUnequip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
                player.getPersistentData().putBoolean(IMMUNITY_KEY, false);
            }
        }

        private static boolean isArmorSlot(EquipmentSlot slot) {
            return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        }


        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                Player player = event.player;
                if (player.getPersistentData().getBoolean(IMMUNITY_KEY)) {
                    List<MobEffectInstance> corrosionEffects = player.getActiveEffects().stream()
                            .filter(effect -> effect.getEffect() instanceof CorrosiveMobEffect)
                            .collect(Collectors.toList());
                    corrosionEffects.forEach(effect -> player.removeEffect(effect.getEffect()));
                }
            }
        }
    }

    static {
        MinecraftForge.EVENT_BUS.register(slushing.class);
    }
}
