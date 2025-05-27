package com.csdy.jzyy.modifier.modifier.jzyy;

import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import static com.csdy.jzyy.JzyyModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChaosTransmigration extends NoLevelsModifier {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        // 获取重生后的玩家对象
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        boolean isEndConquered = event.isEndConquered();
        if (isEndConquered) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            int level = Math.min((int)player.getMaxHealth(),255);
            if (ModifierUtil.getModifierLevel(stack, ModifierRegister.CHAOS_TRANSMIGRATION_STATIC_MODIFIER.getId()) > 0)  player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,1200,level));
        }

    }

}
