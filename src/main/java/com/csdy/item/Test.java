package com.csdy.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class Test extends Item {
    public Test() {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
    }

    private static final Set<String> name = new HashSet();

    @Override
    public void inventoryTick(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        def(entity);
        add(entity);
    }
    @SubscribeEvent
    public void def(TickEvent.PlayerTickEvent e) {
        def(e.player);
    }

    public static boolean isName(Entity entity) {
        return name.contains(entity.getStringUUID());
    }

    public static void add(Entity entity) {
        name.add(entity.getStringUUID());
    }

    @SubscribeEvent
    public void hurt(LivingHurtEvent e) {
        if (e.getEntity() instanceof Player && isName(e.getEntity())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void death(LivingDeathEvent e) {
        if (e.getEntity() instanceof Player && isName(e.getEntity())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void attack(LivingAttackEvent e) {
        if (e.getEntity() instanceof Player && isName(e.getEntity())) {
            e.setCanceled(true);
        }
    }

    public static void def(Entity entity) {
        if (isName(entity)) {
            ((Player) entity).setHealth(20.0F);
            Objects.requireNonNull(((Player) entity).getAttribute(MAX_HEALTH)).setBaseValue(20);
            ((Player) entity).deathTime = 0;
            entity.fallDistance = 0.0F;
            entity.isAddedToWorld = true;
            Minecraft mc = Minecraft.getInstance();
//            if (mc.screen instanceof DeathScreen) {
//                mc.screen = null;
//                mc.setScreen(null);
//            }
        }

    }
    
}
