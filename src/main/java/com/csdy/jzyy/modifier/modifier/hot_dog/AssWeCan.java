package com.csdy.jzyy.modifier.modifier.hot_dog;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceSetAllCandidateHealth;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.setAbsoluteSeveranceHealth;

public class AssWeCan extends NoLevelsModifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        Player player = context.getPlayerAttacker();
        LivingEntity target = context.getLivingTarget();
        if (player instanceof ServerPlayer serverPlayer && target != null){
            if (getPlayerDeathCount(serverPlayer) > 100 && isOniMiko(target)){
                setAbsoluteSeveranceHealth(target,0);
                forceSetAllCandidateHealth(target,0);
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    public static int getPlayerDeathCount(ServerPlayer player) {
        return player.getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS));
    }

    public static boolean isOniMiko(Entity entity) {
        if (entity == null) {
            return false;
        }
        // 方法1：检查实体的命名空间（推荐）
        ResourceLocation entityId = EntityType.getKey(entity.getType());
        if (entityId != null && entityId.getNamespace().equals("oni_miko")) {
            return true;
        }

        // 方法2：检查实体的类路径（备用方案）
        return entity.getClass().getName().contains("oni_miko");
    }
}
