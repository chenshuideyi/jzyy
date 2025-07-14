package com.csdy.jzyy.modifier.modifier;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
public class HellFire extends Modifier implements MeleeDamageModifierHook {

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        float bonusDamage = 0;

        if (context.getLivingTarget() != null) {
            LivingEntity target = context.getLivingTarget();
            Holder<EntityType<?>> holder = ForgeRegistries.ENTITY_TYPES.getHolder(target.getType()).orElse(null);
            if (holder != null && isNetherMob(target)) {
                bonusDamage += 1.1f * modifier.getLevel();
            }
        }
        return damage + bonusDamage;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);

    }

    private boolean isNetherMob(LivingEntity entity) {
        if (entity == null) return false;

        Holder<Biome> biomeHolder = entity.level().getBiome(entity.blockPosition());
        return biomeHolder.is(BiomeTags.IS_NETHER);
    }

}