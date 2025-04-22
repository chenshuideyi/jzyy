package com.csdy.jzyy.modifier.modifier;

import com.csdy.jzyy.ModMain;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.lang.reflect.Method;
import java.util.List;

public class Marisa extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook,TooltipModifierHook, InventoryTickModifierHook, ProjectileLaunchModifierHook {
    private static final ResourceLocation MARISA = new ResourceLocation(ModMain.MODID, "marisa");
    private static final ResourceLocation STEAL = new ResourceLocation(ModMain.MODID, "steal");

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            ModDataNBT data = tool.getPersistentData();
            if(data.getFloat(MARISA) < 100) return;
            dropLoot(target,target.level().damageSources().playerAttack(player));
            data.putFloat(MARISA,0);
        }

    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry entry, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT data, boolean primary) {
        if (!(shooter instanceof Player player)) return;
        if (arrow == null) return;
        ModDataNBT toolPersistentData = tool.getPersistentData();
        if(toolPersistentData.getFloat(MARISA) < 100) return;
        data.putBoolean(STEAL,true);
        toolPersistentData.putFloat(MARISA,0);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT data, ModifierEntry entry, Projectile projectile, EntityHitResult hit, @javax.annotation.Nullable LivingEntity shooter, @javax.annotation.Nullable LivingEntity target) {
        if (projectile instanceof AbstractArrow arrow && target != null) {
            if (!(shooter instanceof Player player)) return false;
            if(!data.getBoolean(STEAL)) return false;
            dropLoot(target,target.level().damageSources().playerAttack(player));
            arrow.remove(Entity.RemovalReason.KILLED);
        }
        return false;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if (player.isSprinting() || player.isFallFlying() || player.getAbilities().flying){
            ModDataNBT data = tool.getPersistentData();
            float value = data.getFloat(MARISA);
            data.putFloat(MARISA,Math.min(value+0.1F+5,100F));
        }
    }


    private static void dropLoot(LivingEntity entity, DamageSource ds) {
        try {
//            Method dropAllDeathLootMethod = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "dropAllDeathLoot", DamageSource.class);
            Method dropAllDeathLootMethod = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_6668_", DamageSource.class);
            dropAllDeathLootMethod.invoke(entity, ds);
        } catch (Exception e) {
            throw new RuntimeException("调用战利品表失败", e);
        }
    }



    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, slimeknights.mantle.client.TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        ModDataNBT persistantData = tool.getPersistentData();
        float value = persistantData.getFloat(MARISA);
        tooltip.add(Component.translatable("marisa.text").withStyle(ChatFormatting.YELLOW).append(value+"%"));
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH);
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
    }


}
