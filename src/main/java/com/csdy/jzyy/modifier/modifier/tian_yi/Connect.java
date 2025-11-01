package com.csdy.jzyy.modifier.modifier.tian_yi;

import com.c2h6s.etstlib.entity.specialDamageSources.PercentageBypassArmorSource;
import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.ArrowHitModifierHook;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.List;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.modifierAbsoluteSeverance;

public class Connect extends NoLevelsModifier implements ArrowHitModifierHook {


    @Override
    public void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
        if (arrow.level().isClientSide) return;
        if (!(attacker instanceof Player player)) return;

        Level level = arrow.level();
        float chainRadius = 16.0f;

        // 查找目标附近的实体
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(target.blockPosition()).inflate(chainRadius),
                entity -> entity != target &&
                        entity != attacker &&
                        entity.isAlive() &&
                        !entity.isAlliedTo(attacker)
        );

        // 对附近实体造成伤害并播放效果
        for (LivingEntity entity : nearbyEntities) {
            modifierAbsoluteSeverance(entity,player,712,1);

            // 播放闪电击中效果
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.3F, 1.0F);

            // 生成视觉效果闪电
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
            if (lightning != null) {
                lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
                lightning.setVisualOnly(true);
                level.addFreshEntity(lightning);
            }
        }

        // 主目标位置播放音效
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.ARROW_HIT);
    }
}
