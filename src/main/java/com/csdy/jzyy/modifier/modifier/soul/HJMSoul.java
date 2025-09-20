package com.csdy.jzyy.modifier.modifier.soul;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.modifierCutting;

public class HJMSoul extends NoLevelsModifier implements MeleeHitModifierHook {

    // 使用Map来存储每个玩家的攻击计数
    private static final Map<UUID, Integer> attackCountMap = new HashMap<>();
    private static final int TRIGGER_COUNT = 4; // 第4次攻击触发效果

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Player player = context.getPlayerAttacker();
        var target = context.getLivingTarget();
        if (player != null && target != null) {
            UUID playerId = player.getUUID();

            // 获取当前攻击计数，如果没有则初始化为0
            int currentCount = attackCountMap.getOrDefault(playerId, 0);
            currentCount++;

            // 检查是否是第4次攻击
            boolean isFourthAttack = (currentCount % TRIGGER_COUNT == 0);

            if (isFourthAttack) {
                // 第四次攻击触发额外效果
                modifierCutting(target,player,99,1);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        JzyySoundsRegister.HAQI.get(), player.getSoundSource(), 0.5F, 1.0F);
            }

            // 更新计数
            attackCountMap.put(playerId, currentCount);
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
