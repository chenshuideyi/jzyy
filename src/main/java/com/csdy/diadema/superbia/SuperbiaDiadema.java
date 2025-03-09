package com.csdy.diadema.superbia;

import com.csdy.diadema.api.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SuperbiaDiadema extends Diadema {
    private final Player player = getPlayer();
    public SuperbiaDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    @Override
    public @NotNull DiademaRange getRange() {
        return new SphereDiademaRange(this, 8);
    }

    @Override
    protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!entity.equals(player) && living.getHealth()<player.getMaxHealth()) {
                living.die(new DamageSource(living.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), player));
                living.setHealth(0);
                AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                double originalMaxHealth = maxHealthAttr.getBaseValue();
                double reducedMaxHealth = originalMaxHealth + 2;
                maxHealthAttr.setBaseValue(reducedMaxHealth);
            }
        }
    }
}
