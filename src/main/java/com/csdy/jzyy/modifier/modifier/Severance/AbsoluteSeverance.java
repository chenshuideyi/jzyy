package com.csdy.jzyy.modifier.modifier.Severance;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;

import static com.csdy.jzyy.method.LivingEntityUtil.*;

public class AbsoluteSeverance extends NoLevelsModifier implements MeleeHitModifierHook {

    private final float value;

    public AbsoluteSeverance(float value) {
        this.value = value;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null) {
            float damage = target.getHealth() - damageDealt * this.value;
            setAbsoluteSeveranceHealth(target,damage);
            System.out.println("绝对切断！");
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    /**
     * 监听世界刻更新：每刻在服务器端遍历绝对绝对切断伤害记录，
     * 如果对应实体存在且当前生命值与预期不符，则持续覆盖其生命值；
     * 若实体不存在，则自动清除记录。
     */
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel serverLevel)) return;
        for (UUID uuid : getAbsoluteSeveranceHealthMap().keySet()) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                float expected = getAbsoluteSeveranceHealth(living);
                if (!Float.isNaN(expected) && Math.abs(living.getHealth() - expected) > 0.1f) {
                    forceSetAllCandidateHealth(living, expected);
                }
            } else {
                clearAbsoluteSeveranceHealth(uuid);
            }
        }
    }


}
