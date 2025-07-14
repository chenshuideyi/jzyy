package com.csdy.jzyy.modifier.modifier.diexv;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;


public class Diexv extends Modifier implements MeleeDamageModifierHook {
    public Diexv(){}

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this,ModifierHooks.MELEE_DAMAGE);
    }
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        if (target != null) {
            target.hurt(target.damageSources().playerAttack((Player) context.getAttacker()), (float) Double.POSITIVE_INFINITY);//设置伤害为正无穷
            target.die(target.damageSources().playerAttack((Player) context.getAttacker()));//设置死亡
            target.setHealth(0); //设置血量为0
            if (context.getAttacker() instanceof net.minecraft.world.entity.player.Player) {
                net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player) context.getAttacker();
                // 生成多倍掉落物
                if (target.isDeadOrDying()) {
                    LootParams lootParams = new LootParams.Builder((ServerLevel) target.level())
                            .withParameter(LootContextParams.THIS_ENTITY, target)
                            .withParameter(LootContextParams.ORIGIN, target.position())
                            .withParameter(LootContextParams.DAMAGE_SOURCE, target.damageSources().playerAttack(player))
                            .create(LootContextParamSets.ENTITY);

                    LootTable lootTable = target.level().getServer().getLootData().getLootTable(target.getLootTable());
                    if (lootTable != LootTable.EMPTY) {
                        List<ItemStack> drops = lootTable.getRandomItems(lootParams);
                        drops.forEach(stack -> {
                            ItemStack multipliedStack = stack.copy();
                            multipliedStack.setCount(stack.getCount() * 64);
                            target.spawnAtLocation(multipliedStack);
                        });
                    }
                }
                if (target.shouldDropExperience()) {
                    int baseExp = target.getExperienceReward();
                    if (baseExp > 0) {
                        // 生成多倍经验球（合并为单个大经验球）
                        ExperienceOrb.award((ServerLevel) target.level(),
                                target.position(),
                                baseExp * 64);
                    }
                }
            }
            }
            return damage;
        }
    }